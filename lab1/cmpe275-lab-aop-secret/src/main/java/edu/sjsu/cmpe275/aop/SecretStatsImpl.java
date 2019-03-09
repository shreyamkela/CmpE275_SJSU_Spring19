package edu.sjsu.cmpe275.aop;

import java.util.HashMap;
import java.util.HashSet;

public class SecretStatsImpl implements SecretStats {
	/***
	 * Following is a dummy implementation. You are expected to provide an actual implementation based on the requirements.
	 */

	public int lengthOfLongestSecret = 0;
	public String mostTrustedUser = null;
	public String bestKnownSecret = null;
	public boolean permanentNetworkFailure = false;

	// Alice shares secret A with Bob
	// Alice shares secret B with Bob
	// Outer Hashmap key = Bob, value is Alice (which is inner hashmap key). The value of inner hashmap is a treeset with keys A and B
	public HashMap<String, HashMap<String, HashSet<String>>> sharedSecrets = new HashMap<String, HashMap<String, HashSet<String>>>();

	@Override
	public void resetStatsAndSystem() {
		lengthOfLongestSecret = 0;
		mostTrustedUser = null;
		bestKnownSecret = null;
		permanentNetworkFailure = false;
	}

	@Override
	public int getLengthOfLongestSecret() {
		return lengthOfLongestSecret;
	}

	@Override
	public String getMostTrustedUser() {
		if (sharedSecrets != null) {
			HashMap<String, HashSet<String>> innerHashMap = new HashMap<String, HashSet<String>>();
			HashSet<String> innerHashSet = new HashSet<String>();

			int maxSharingOccurences = 0;

			for (String keyOuterMap : sharedSecrets.keySet()) {
				int temp = 0;
				innerHashMap = sharedSecrets.get(keyOuterMap);
				for (String keyInnerMap : innerHashMap.keySet()) {
					innerHashSet = innerHashMap.get(keyInnerMap);
					temp = temp + innerHashSet.size();
				}
				if (maxSharingOccurences < temp) {
					maxSharingOccurences = temp;
					mostTrustedUser = keyOuterMap;
				} else if (maxSharingOccurences == temp && maxSharingOccurences != 0) {
					// Checking for a tie case
					if (mostTrustedUser.compareTo(keyOuterMap) > 0) {
						mostTrustedUser = keyOuterMap;
					}
				}
			}
		}
		return mostTrustedUser;
	}

	@Override
	public String getWorstSecretKeeper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBestKnownSecret() {
		// TODO Auto-generated method stub
		return bestKnownSecret;
	}

}
