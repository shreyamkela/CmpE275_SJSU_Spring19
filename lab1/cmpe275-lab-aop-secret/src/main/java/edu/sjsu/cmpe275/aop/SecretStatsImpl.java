package edu.sjsu.cmpe275.aop;

import java.util.HashMap;
import java.util.HashSet;

public class SecretStatsImpl implements SecretStats {
	/***
	 * Following is a dummy implementation. You are expected to provide an actual implementation based on the requirements.
	 */

	public int longestOfLongestSecret = 0;

	// Alice shares secret A with Bob
	// Alice shares secret B with Bob
	// Outer Hashmap key = Bob, value is Alice (which is inner hashmap key). The value of inner hashmap is a treeset with keys A and B
	public HashMap<String, HashMap<String, HashSet<String>>> sharedSecrets = new HashMap<String, HashMap<String, HashSet<String>>>();

	@Override
	public void resetStatsAndSystem() {
		// TODO Auto-generated method stub
		longestOfLongestSecret = 0;

	}

	@Override
	public int getLengthOfLongestSecret() {
		return longestOfLongestSecret;
	}

	@Override
	public String getMostTrustedUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorstSecretKeeper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBestKnownSecret() {
		// TODO Auto-generated method stub
		return null;
	}

}
