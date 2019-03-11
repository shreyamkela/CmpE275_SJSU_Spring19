/**
* @author Shreyam Kela - Student Id 013775411
*/

package edu.sjsu.cmpe275.aop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SecretStatsImpl implements SecretStats {

	public int lengthOfLongestSecret = 0;
	public String mostTrustedUser = null;
	public String worstSecretKeeper = null;
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

	// For worst secret keeper (similar to Most trusted user):
	// Alice shares secret A with Bob
	// Alice shares secret B with Bob
	// Outer Hashmap key = Alice, value is Bob (which is inner hashmap key). The value of inner hashmap is a hashset with keys A and B
	public HashMap<String, HashMap<String, HashSet<String>>> creatorSecrets = new HashMap<String, HashMap<String, HashSet<String>>>();

	// For access control - check whether a user has access to a secret or not:
	// Alice creates a secret A
	// Outer Hashmap key = Alice, value is inner hashset with key A
	// Alice shares secret A with Bob
	// Outer Hashmap key = Bob, value is inner hashset with key A
	public HashMap<String, HashSet<String>> accessToSecrets = new HashMap<String, HashSet<String>>();

	@Override
	public void resetStatsAndSystem() {
		lengthOfLongestSecret = 0;
		mostTrustedUser = null;
		worstSecretKeeper = null;
		bestKnownSecret = null;
		sharedSecrets.clear();
		knownSecrets.clear();
		secretIdWithCreatorAndContent.clear();
		creatorSecrets.clear();
		accessToSecrets.clear();
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
				if (sharedSecrets.get(keyOuterMap) != null) { // sharedSecrets should have a key then only we move forwards
					innerHashMap = sharedSecrets.get(keyOuterMap);
					for (String keyInnerMap : innerHashMap.keySet()) {
						if (innerHashMap.get(keyInnerMap) != null) {
							innerHashSet = innerHashMap.get(keyInnerMap);

							temp = temp + innerHashSet.size();
						}

					}
				}

				if (maxSharingOccurences < temp) {
					maxSharingOccurences = temp;
					mostTrustedUser = keyOuterMap;
				} else if (maxSharingOccurences == temp && maxSharingOccurences != 0 && mostTrustedUser != null) {
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

		if (sharedSecrets != null && creatorSecrets != null) {

			HashMap<String, HashSet<String>> innerHashMap = new HashMap<String, HashSet<String>>();
			HashSet<String> innerHashSet = new HashSet<String>();

			HashMap<String, HashSet<String>> innerHashMap1 = new HashMap<String, HashSet<String>>();
			HashSet<String> innerHashSet1 = new HashSet<String>();

			int secretKeepingScore = 0, count = 0;

			for (String keyOuterMap : creatorSecrets.keySet()) {
				int secretSharingCount = 0, secretReceiveingCount = 0;

				if (creatorSecrets.get(keyOuterMap) != null) { // sharedSecrets should have a key then only we move forwards
					innerHashMap = creatorSecrets.get(keyOuterMap);
					for (String keyInnerMap : innerHashMap.keySet()) {
						if (innerHashMap.get(keyInnerMap) != null) {
							innerHashSet = innerHashMap.get(keyInnerMap);
							secretSharingCount = secretSharingCount + innerHashSet.size();
						}

					}
				}

				if (sharedSecrets.get(keyOuterMap) != null) {
					innerHashMap1 = sharedSecrets.get(keyOuterMap);
					for (String keyInnerMap : innerHashMap1.keySet()) {
						if (innerHashMap1.get(keyInnerMap) != null) {
							innerHashSet1 = innerHashMap1.get(keyInnerMap);
							secretReceiveingCount = secretReceiveingCount + innerHashSet1.size();
						}
					}
				}

				if (count == 0) {
					secretKeepingScore = secretReceiveingCount - secretSharingCount;
					worstSecretKeeper = keyOuterMap;
					count++;
				} else {
					if ((secretReceiveingCount - secretSharingCount) < secretKeepingScore) {
						secretKeepingScore = secretReceiveingCount - secretSharingCount;
						worstSecretKeeper = keyOuterMap;
					} else if ((secretReceiveingCount - secretSharingCount) == secretKeepingScore
							&& (secretReceiveingCount != 0 || secretSharingCount != 0) && worstSecretKeeper != null) {
						if (worstSecretKeeper.compareTo(keyOuterMap) > 0) {

							worstSecretKeeper = keyOuterMap;

						}
					}
				}

			}
		}
		return worstSecretKeeper;

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
//					System.out.println(secretIdWithCreatorAndContent);
//					System.out.println(key);
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