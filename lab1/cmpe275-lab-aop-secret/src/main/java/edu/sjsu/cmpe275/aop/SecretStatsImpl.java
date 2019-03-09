package edu.sjsu.cmpe275.aop;

import java.util.ArrayList;
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

	// For Most trusted user:
	// Alice shares secret A with Bob
	// Alice shares secret B with Bob
	// Outer Hashmap key = Bob, value is Alice (which is inner hashmap key). The value of inner hashmap is a hashset with keys A and B
	public HashMap<String, HashMap<String, HashSet<String>>> sharedSecrets = new HashMap<String, HashMap<String, HashSet<String>>>();

	// For best known secret:
	// Alice shares secret A with Bob
	// Alice shares secret A with Carl
	// Hashmap key = A, value is a hashset with keys Bob & Carl
	public HashMap<String, HashSet<String>> knownSecrets = new HashMap<String, HashSet<String>>();

	// For keeping a record of all secret contents and creatorID with their UUID Strings :
	public HashMap<String, ArrayList<String>> secretIdWithCreatorAndContent = new HashMap<String, ArrayList<String>>();

	@Override
	public void resetStatsAndSystem() {
		lengthOfLongestSecret = 0;
		mostTrustedUser = null;
		bestKnownSecret = null;
		sharedSecrets = new HashMap<String, HashMap<String, HashSet<String>>>();
		knownSecrets = new HashMap<String, HashSet<String>>();
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
		if (knownSecrets != null) {
			HashSet<String> innerHashSet = new HashSet<String>();

			int bestKnownCount = 0;

			for (String key : knownSecrets.keySet()) {
				innerHashSet = knownSecrets.get(key);

				if (innerHashSet.size() == 1) {
					// innerHashSet contains all the user that this secret was shared with. If innerhashset.soze == 1 and innerhashset contains only the creator of this secret himself, then it means no one has read this secret and only creator knows about this secret,
					// therefore we dont count this case
					String creator = null;
					System.out.println(secretIdWithCreatorAndContent);
					System.out.println(key);

					for (String keyId : secretIdWithCreatorAndContent.keySet()) {
						if (secretIdWithCreatorAndContent.get(keyId).get(1) == key) {
							creator = secretIdWithCreatorAndContent.get(keyId).get(0);
						}
					}
					if (innerHashSet.contains(creator)) {
						continue;
					}

				}

				if (bestKnownCount < innerHashSet.size()) {
					bestKnownCount = innerHashSet.size();
					bestKnownSecret = key;
				} else if (bestKnownCount == innerHashSet.size() && bestKnownCount != 0) {
					// Checking for a tie case
					if (bestKnownSecret.compareTo(key) > 0) {
						bestKnownSecret = key;
					}
				}
			}
		}
		return bestKnownSecret;
	}
}