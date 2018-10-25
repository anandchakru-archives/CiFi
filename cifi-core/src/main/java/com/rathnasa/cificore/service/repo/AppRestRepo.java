package com.rathnasa.cificore.service.repo;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import com.rathnasa.cificore.entity.App;

@RepositoryRestResource(path = "app")
public interface AppRestRepo extends PagingAndSortingRepository<App, Long> {
	@RestResource(path = "by", rel = "by") //	http://localhost:8078/repo/app/search/by?name=jrvite
	List<App> findByAppName(@Param("name") String appName);
}