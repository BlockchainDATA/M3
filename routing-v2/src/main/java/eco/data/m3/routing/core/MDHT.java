package eco.data.m3.routing.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eco.data.m3.content.MContent;
import eco.data.m3.content.MContentKey;
import eco.data.m3.content.MContentManager;
import eco.data.m3.content.MContentMeta;
import eco.data.m3.content.data.StorageEntry;
import eco.data.m3.content.data.StorageEntryState;

public class MDHT {

    private MContentManager contentManager;
    
    public MDHT(MContentManager contentManager)
    {
    	this.contentManager = contentManager;
    }
    
    public MContentManager getContentManger() {
    	return this.contentManager;
    }
    
    public MContent<?> getContent(MContentKey key) throws Exception {
    	StorageEntry entry = contentManager.get(key, StorageEntryState.Normal);
    	if(entry==null)
    		return null;
		return contentManager.getContent(entry);    	
    }
    
    public boolean contains(MContentKey key) {
		return contentManager.contains(key , StorageEntryState.Normal);    	
    }
    
    public boolean tryStore(MContentKey key) {
    	return contentManager.tryStore(key);
    }
    
    public boolean tryStore(MContentMeta meta) {
    	return contentManager.tryStore(meta);
    }
    
    public void put(MContentMeta meta) throws Exception {
    	contentManager.put(meta);
    }
    
    public short openFD(MContentMeta meta) throws Exception {
    	StorageEntry entry = contentManager.get(meta, null);
    	if(entry==null)
    		entry = contentManager.put(meta);
    	return contentManager.openFD(entry, false);
    }
    
    public void closeFD(short fd) throws IOException {
    	contentManager.closeFD(fd);
    }
    
    public void finishStore(short fd) throws Exception {
    	contentManager.finishStore(fd);
    }
    
    public void writeData(short fd, int start, byte[] data) throws IOException {
    	contentManager.writeStorageData(fd, start, data);
    }

    public void store(MContent<?> content) throws Exception {
    	short fd = openFD(content.getMeta());
    	writeData(fd, 0, content.getData());
    	finishStore(fd);
    	closeFD(fd);
    }
    
    public void remove(MContent<?> content) {
    	remove(content.getMeta());
    }
    
    public void remove(MContentMeta meta) {
    	contentManager.remove(new MContentKey(meta), StorageEntryState.Normal);
    }
    
    public List<MContentMeta> getContents()
    {
        List<StorageEntry> entries = contentManager.entries();
        List<MContentMeta> metas = new ArrayList<>();
        for (StorageEntry entry : entries) {
			metas.add(entry.getMeta());
		}
        return metas;
    }

    @Override
    public String toString()
    {
        return this.contentManager.toString();
    }
    
}
