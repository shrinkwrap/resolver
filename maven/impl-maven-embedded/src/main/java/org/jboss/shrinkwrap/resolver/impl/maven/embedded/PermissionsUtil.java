package org.jboss.shrinkwrap.resolver.impl.maven.embedded;

import java.io.File;

public class PermissionsUtil {

    private static final int OWNER_READ_FLAG = 0400;
    private static final int OWNER_WRITE_FLAG = 0200;
    private static final int OWNER_EXECUTE_FLAG = 0100;

    private static final int GROUP_READ_FLAG = 0040;
    private static final int GROUP_WRITE_FLAG = 0020;
    private static final int GROUP_EXECUTE_FLAG = 0010;

    private static final int OTHERS_READ_FLAG = 0004;
    private static final int OTHERS_WRITE_FLAG = 0002;
    private static final int OTHERS_EXECUTE_FLAG = 0001;

    public static FilePermission toFilePermission(int mode) {

        int maskedMode = mode & 0777;
        FilePermission filePermission = new FilePermission();

        if ((maskedMode & OWNER_READ_FLAG) > 0) {
            filePermission.setOwnerCanRead(true);
        }
        if ((maskedMode & OWNER_WRITE_FLAG) > 0) {
            filePermission.setOwnerCanWrite(true);
        }
        if ((maskedMode & OWNER_EXECUTE_FLAG) > 0) {
            filePermission.setOwnerCanExecute(true);
        }

        if ((maskedMode & GROUP_READ_FLAG) > 0) {
            filePermission.setGroupCanRead(true);
        }
        if ((maskedMode & GROUP_WRITE_FLAG) > 0) {
            filePermission.setGroupCanWrite(true);
        }
        if ((maskedMode & GROUP_EXECUTE_FLAG) > 0) {
            filePermission.setGroupCanExecute(true);
        }

        if ((maskedMode & OTHERS_READ_FLAG) > 0) {
            filePermission.setOthersCanRead(true);
        }
        if ((maskedMode & OTHERS_WRITE_FLAG) > 0) {
            filePermission.setOthersCanWrite(true);
        }
        if ((maskedMode & OTHERS_EXECUTE_FLAG) > 0) {
            filePermission.setOthersCanExecute(true);
        }

        return filePermission;
    }

    public static void applyPermission(File file, FilePermission permissions) {
        boolean executableSet = file.setExecutable(permissions.isOwnerCanExecute(),!permissions.isGroupCanExecute() && !permissions.isOthersCanExecute());
        boolean writableSet = file.setWritable(permissions.isOwnerCanWrite(),!permissions.isGroupCanWrite() && !permissions.isOthersCanWrite());
        boolean readableSet = file.setReadable(permissions.isOwnerCanRead(), !permissions.isGroupCanRead() && !permissions.isOthersCanRead());

        if (!executableSet || !writableSet || !readableSet) {
            System.err.println("Unable to change permissions of extracted files.");
        }
    }
}
