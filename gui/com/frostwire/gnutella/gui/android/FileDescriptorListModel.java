package com.frostwire.gnutella.gui.android;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

public class FileDescriptorListModel extends AbstractListModel {

    /**
     * 
     */
    private static final long serialVersionUID = 3826940677788298380L;

    private List<FileDescriptor> _fileDescriptors;
    private List<FileDescriptor> _filterFileDescriptors;
    
    private String _filterText;

    public FileDescriptorListModel() {
        _fileDescriptors = new ArrayList<FileDescriptor>();
        _filterFileDescriptors = new ArrayList<FileDescriptor>();
    }

    public void clear() {
        _filterText = null;
        int index = _filterFileDescriptors.size() - 1;
        _fileDescriptors.clear();
        _filterFileDescriptors.clear();
        if (index >= 0) {
            fireIntervalRemoved(this, 0, index);
        }
    }
    
    public void addAll(List<FileDescriptor> fileDescriptors) {
        _fileDescriptors.addAll(fileDescriptors);
        refilter();
    }

    public void update(FileDescriptor fileDescriptor) {
        int index = _filterFileDescriptors.indexOf(fileDescriptor);
        fireContentsChanged(this, index, index);
    }

    /**
     * This method is designed to be called by a no GUI thread.
     * 
     * @param filterText Text patter to perform the query search.
     */
    public void filter(String filterText) {
        _filterText = filterText;
        refilter();
    }

    @Override
    public int getSize() {
        return _filterFileDescriptors.size();
    }

    @Override
    public Object getElementAt(int index) {
        if (index >= 0 && index < _filterFileDescriptors.size()) {
            return _filterFileDescriptors.get(index);
        } else {
            return null;
        }
    }
    
    private void refilter() {
        
        if (Thread.interrupted()) {
            return;
        }
        
        _filterFileDescriptors.clear();
        
        if (_filterText == null || _filterText.trim().length() == 0) {
            
            if (Thread.interrupted()) {
                return;
            }
            
            _filterFileDescriptors.addAll(_fileDescriptors);
        } else {
            String searchText = _filterText.trim().toLowerCase();
            
            for (int i = 0; i < _fileDescriptors.size(); i++) {
                
                if (Thread.interrupted()) {
                    return;
                }
                
                FileDescriptor fileDescriptor = _fileDescriptors.get(i);
                
                String title = fileDescriptor.title != null ? fileDescriptor.title.toLowerCase() : "";
                String artist = fileDescriptor.artist != null ? fileDescriptor.artist.toLowerCase() : "";
                String album = fileDescriptor.album != null ? fileDescriptor.album.toLowerCase() : "";
                
                boolean isSubString = title.contains(searchText)|| artist.contains(searchText) || album.contains(searchText);
                
                if (isSubString) {
                    _filterFileDescriptors.add(fileDescriptor);
                }
            }
            
            if (_filterFileDescriptors.size() == 0) {
                
                for (int i = 0; i < _fileDescriptors.size(); i++) {
                    
                    if (Thread.interrupted()) {
                        return;
                    }
                    
                    FileDescriptor fileDescriptor = _fileDescriptors.get(i);
                    
                    String title = fileDescriptor.title != null ? fileDescriptor.title.toLowerCase() : "";
                    String artist = fileDescriptor.artist != null ? fileDescriptor.artist.toLowerCase() : "";
                    String album = fileDescriptor.album != null ? fileDescriptor.album.toLowerCase() : "";
                    
                    if (pseudoMatch(title, searchText) &&
                        pseudoMatch(artist, searchText) &&
                        pseudoMatch(album, searchText)) {
                        _filterFileDescriptors.add(fileDescriptor);
                    }
                }
            }
            
            if (_filterFileDescriptors.size() == 0) {
                
                for (int i = 0; i < _fileDescriptors.size(); i++) {
                    
                    if (Thread.interrupted()) {
                        return;
                    }
                    
                    FileDescriptor fileDescriptor = _fileDescriptors.get(i);
                    
                    String title = fileDescriptor.title != null ? fileDescriptor.title.toLowerCase() : "";
                    String artist = fileDescriptor.artist != null ? fileDescriptor.artist.toLowerCase() : "";
                    String album = fileDescriptor.album != null ? fileDescriptor.album.toLowerCase() : "";
                    
                    if (pseudoMatch(title, searchText) ||
                        pseudoMatch(artist, searchText) ||
                        pseudoMatch(album, searchText)) {
                        _filterFileDescriptors.add(fileDescriptor);
                    }
                }
            }
        }
        
        if (Thread.interrupted()) {
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireContentsChanged(this, 0, getSize());
            }
        });
    }

    private boolean pseudoMatch(String text, String searchText) {
        
        if (Thread.interrupted()) {
            return false;
        }
        
        if (text == null || text.length() == 0) {
            return false;
        }
        
        String[] tokens = searchText.split(" ");
        
        for (int i = 0; i < tokens.length; i++) {
            
            if (Thread.interrupted()) {
                return false;
            }
            
            if (text.contains(tokens[i])) {
                return true;
            }
        }
        
        return false;
    }
}
