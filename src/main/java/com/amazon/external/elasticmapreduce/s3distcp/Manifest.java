package com.amazon.external.elasticmapreduce.s3distcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manifest {

	private Map<String, Map<Long, ManifestEntry>> manifestByPartial = new HashMap<String, Map<Long, ManifestEntry>>();
	private Map<String, ManifestEntry> manifestByFullPath = new HashMap<String, ManifestEntry>();
	private List<ManifestEntry> manifests = new ArrayList<ManifestEntry>();
	private boolean basePathEntries = false;
	
	public void addManifest(ManifestEntry entry) {
		manifests.add(entry);
		if (entry.srcPath != null) {
			manifestByFullPath.put(entry.srcPath, entry);
		} else {
			if (!manifestByPartial.containsKey(entry.baseName))				
				manifestByPartial.put(entry.baseName, new HashMap<Long, ManifestEntry>());
			manifestByPartial.get(entry.baseName).put(entry.size, entry);
			basePathEntries |= entry.baseName.contains("/");
		}
	}
	
	public ManifestEntry getManifest(String srcPath, String baseName, String fileName, long fileSize) {
		if (!manifestByFullPath.isEmpty()) {
			ManifestEntry entry = manifestByFullPath.get(srcPath);
			if (entry != null && entry.size == fileSize)
				return entry;
		} else {
			Map<Long, ManifestEntry> entries;
			if (basePathEntries) 
				entries = manifestByPartial.get(baseName);
			else 
				entries = manifestByPartial.get(fileName);
			if (entries != null && entries.containsKey(fileSize))
				return entries.get(fileSize);
		}
		return null;
	}
	
	public List<ManifestEntry> getEntries() {
		return manifests;
	}
}
