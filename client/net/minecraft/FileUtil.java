/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;

public class FileUtil {
    private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
    private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

    public static String findAvailableName(Path path, String string, String string2) throws IOException {
        for (Object object : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
            string = string.replace((char)object, '_');
        }
        if (RESERVED_WINDOWS_FILENAMES.matcher(string = string.replaceAll("[./\"]", "_")).matches()) {
            string = "_" + string + "_";
        }
        Matcher matcher = COPY_COUNTER_PATTERN.matcher(string);
        int n = 0;
        if (matcher.matches()) {
            string = matcher.group("name");
            n = Integer.parseInt(matcher.group("count"));
        }
        if (string.length() > 255 - string2.length()) {
            string = string.substring(0, 255 - string2.length());
        }
        do {
            Object object;
            String string3 = string;
            if (n != 0) {
                String string4 = " (" + n + ")";
                int n2 = 255 - string4.length();
                if (string3.length() > n2) {
                    string3 = string3.substring(0, n2);
                }
                string3 = string3 + string4;
            }
            string3 = string3 + string2;
            object = path.resolve(string3);
            try {
                Path path2 = Files.createDirectory((Path)object, new FileAttribute[0]);
                Files.deleteIfExists(path2);
                return path.relativize(path2).toString();
            }
            catch (FileAlreadyExistsException fileAlreadyExistsException) {
                ++n;
                continue;
            }
            break;
        } while (true);
    }

    public static boolean isPathNormalized(Path path) {
        Path path2 = path.normalize();
        return path2.equals(path);
    }

    public static boolean isPathPortable(Path path) {
        for (Path path2 : path) {
            if (!RESERVED_WINDOWS_FILENAMES.matcher(path2.toString()).matches()) continue;
            return false;
        }
        return true;
    }

    public static Path createPathToResource(Path path, String string, String string2) {
        String string3 = string + string2;
        Path path2 = Paths.get(string3, new String[0]);
        if (path2.endsWith(string2)) {
            throw new InvalidPathException(string3, "empty resource name");
        }
        return path.resolve(path2);
    }
}

