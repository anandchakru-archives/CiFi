package com.rathnasa.cificore.service.sign;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 
 * @author chakru
 *
 */
@Service
public class GithubSignatureVerifier {
	private static final Logger logger = LoggerFactory
			.getLogger("com.rathnasa.cificore.service.sign.GithubSignatureVerifier");

	public boolean verify(String source, String signature, String token) {
		if (token == null || token.length() == 0 || signature == null || source == null || source.length() == 0) {
			return false;
		}
		String encodedKey = sign(source, token);
		logger.debug(
				"Verifying Github:" + source + ",signature:" + signature + ", token:" + token + ",sha1:" + encodedKey);
		return encodedKey.equals(signature);
	}
	public String sign(String source, String token) {
		return "sha1=" + new HmacUtils(HmacAlgorithms.HMAC_SHA_1, token).hmacHex(source);
	}
}