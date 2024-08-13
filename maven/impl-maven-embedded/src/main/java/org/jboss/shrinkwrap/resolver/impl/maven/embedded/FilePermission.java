package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

public class FilePermission {

    private boolean ownerCanRead;
    private boolean ownerCanWrite;
    private boolean ownerCanExecute;

    private boolean groupCanRead;
    private boolean groupCanWrite;
    private boolean groupCanExecute;

    private boolean othersCanRead;
    private boolean othersCanWrite;
    private boolean othersCanExecute;

    public boolean isOwnerCanRead() {
        return ownerCanRead;
    }

    public void setOwnerCanRead(boolean ownerCanRead) {
        this.ownerCanRead = ownerCanRead;
    }

    public boolean isOwnerCanWrite() {
        return ownerCanWrite;
    }

    public void setOwnerCanWrite(boolean ownerCanWrite) {
        this.ownerCanWrite = ownerCanWrite;
    }

    public boolean isOwnerCanExecute() {
        return ownerCanExecute;
    }

    public void setOwnerCanExecute(boolean ownerCanExecute) {
        this.ownerCanExecute = ownerCanExecute;
    }

    public boolean isGroupCanRead() {
        return groupCanRead;
    }

    public void setGroupCanRead(boolean groupCanRead) {
        this.groupCanRead = groupCanRead;
    }

    public boolean isGroupCanWrite() {
        return groupCanWrite;
    }

    public void setGroupCanWrite(boolean groupCanWrite) {
        this.groupCanWrite = groupCanWrite;
    }

    public boolean isGroupCanExecute() {
        return groupCanExecute;
    }

    public void setGroupCanExecute(boolean groupCanExecute) {
        this.groupCanExecute = groupCanExecute;
    }

    public boolean isOthersCanRead() {
        return othersCanRead;
    }

    public void setOthersCanRead(boolean othersCanRead) {
        this.othersCanRead = othersCanRead;
    }

    public boolean isOthersCanWrite() {
        return othersCanWrite;
    }

    public void setOthersCanWrite(boolean othersCanWrite) {
        this.othersCanWrite = othersCanWrite;
    }

    public boolean isOthersCanExecute() {
        return othersCanExecute;
    }

    public void setOthersCanExecute(boolean othersCanExecute) {
        this.othersCanExecute = othersCanExecute;
    }
}
