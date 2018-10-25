package com.rathnasa.cificore.service.repo;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import com.rathnasa.cificore.entity.History;
import com.rathnasa.cifimodel.enums.BuildStatusType;

@RepositoryRestResource(path = "history")
public interface HistoryRestRepo extends PagingAndSortingRepository<History, Long> {
	public History findByCommitId(String commitId);
	public History findByCommitIdAndApp_AppId(String commitId, Long appId);
	//
	@RestResource(path = "byAppName", rel = "byAppName") //eg:	http://localhost:8078/repo/history/search/byAppName?name=jrvite
	List<History> findByApp_AppNameOrderByLatestDesc(@Param("name") String appName);
	@RestResource(path = "byAppId", rel = "byAppId") //eg:	http://localhost:8078/repo/history/search/byAppId?id=1
	List<History> findByApp_AppId(@Param("id") Long appId);
	//
	@Modifying
	@Transactional
	@RestResource(exported = false)
	@Query("update History a set a.status=?2 where a.commitId = ?1")
	public void setFixedStatusFor(String commitId, BuildStatusType status);
	@Modifying
	@Transactional
	@RestResource(exported = false)
	@Query("update History a set a.assetId=?2, a.assetUrl=?3, a.status=?4, a.tag=?5, a.version=?6, a.latest=?7 where a.historyId = ?1")
	public void setFixedAssetStatusTagVersionLatestFor(Long historyId, String assetId, String assetUrl,
			BuildStatusType status, String tag, String version, Boolean latest);
	@Modifying
	@Transactional
	@RestResource(exported = false)
	@Query("update History a set a.latest=?1")
	public void setFixedLatest(Boolean latest);
}