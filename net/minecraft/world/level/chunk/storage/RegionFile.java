/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.level.chunk.storage;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.RegionBitmap;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ByteBuffer PADDING_BUFFER = ByteBuffer.allocateDirect(1);
    private final FileChannel file;
    private final Path externalFileDir;
    private final RegionFileVersion version;
    private final ByteBuffer header = ByteBuffer.allocateDirect(8192);
    private final IntBuffer offsets;
    private final IntBuffer timestamps;
    @VisibleForTesting
    protected final RegionBitmap usedSectors = new RegionBitmap();

    public RegionFile(File file, File file2, boolean bl) throws IOException {
        this(file.toPath(), file2.toPath(), RegionFileVersion.VERSION_DEFLATE, bl);
    }

    public RegionFile(Path path, Path path2, RegionFileVersion regionFileVersion, boolean bl) throws IOException {
        this.version = regionFileVersion;
        if (!Files.isDirectory(path2, new LinkOption[0])) {
            throw new IllegalArgumentException("Expected directory, got " + path2.toAbsolutePath());
        }
        this.externalFileDir = path2;
        this.offsets = this.header.asIntBuffer();
        this.offsets.limit(1024);
        this.header.position(4096);
        this.timestamps = this.header.asIntBuffer();
        this.file = bl ? FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.DSYNC) : FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
        this.usedSectors.force(0, 2);
        this.header.position(0);
        int n = this.file.read(this.header, 0L);
        if (n != -1) {
            if (n != 8192) {
                LOGGER.warn("Region file {} has truncated header: {}", (Object)path, (Object)n);
            }
            long l = Files.size(path);
            for (int i = 0; i < 1024; ++i) {
                int n2 = this.offsets.get(i);
                if (n2 == 0) continue;
                int n3 = RegionFile.getSectorNumber(n2);
                int n4 = RegionFile.getNumSectors(n2);
                if (n3 < 2) {
                    LOGGER.warn("Region file {} has invalid sector at index: {}; sector {} overlaps with header", (Object)path, (Object)i, (Object)n3);
                    this.offsets.put(i, 0);
                    continue;
                }
                if (n4 == 0) {
                    LOGGER.warn("Region file {} has an invalid sector at index: {}; size has to be > 0", (Object)path, (Object)i);
                    this.offsets.put(i, 0);
                    continue;
                }
                if ((long)n3 * 4096L > l) {
                    LOGGER.warn("Region file {} has an invalid sector at index: {}; sector {} is out of bounds", (Object)path, (Object)i, (Object)n3);
                    this.offsets.put(i, 0);
                    continue;
                }
                this.usedSectors.force(n3, n4);
            }
        }
    }

    private Path getExternalChunkPath(ChunkPos chunkPos) {
        String string = "c." + chunkPos.x + "." + chunkPos.z + ".mcc";
        return this.externalFileDir.resolve(string);
    }

    @Nullable
    public synchronized DataInputStream getChunkDataInputStream(ChunkPos chunkPos) throws IOException {
        int n = this.getOffset(chunkPos);
        if (n == 0) {
            return null;
        }
        int n2 = RegionFile.getSectorNumber(n);
        int n3 = RegionFile.getNumSectors(n);
        int n4 = n3 * 4096;
        ByteBuffer byteBuffer = ByteBuffer.allocate(n4);
        this.file.read(byteBuffer, n2 * 4096);
        byteBuffer.flip();
        if (byteBuffer.remaining() < 5) {
            LOGGER.error("Chunk {} header is truncated: expected {} but read {}", (Object)chunkPos, (Object)n4, (Object)byteBuffer.remaining());
            return null;
        }
        int n5 = byteBuffer.getInt();
        byte by = byteBuffer.get();
        if (n5 == 0) {
            LOGGER.warn("Chunk {} is allocated, but stream is missing", (Object)chunkPos);
            return null;
        }
        int n6 = n5 - 1;
        if (RegionFile.isExternalStreamChunk(by)) {
            if (n6 != 0) {
                LOGGER.warn("Chunk has both internal and external streams");
            }
            return this.createExternalChunkInputStream(chunkPos, RegionFile.getExternalChunkVersion(by));
        }
        if (n6 > byteBuffer.remaining()) {
            LOGGER.error("Chunk {} stream is truncated: expected {} but read {}", (Object)chunkPos, (Object)n6, (Object)byteBuffer.remaining());
            return null;
        }
        if (n6 < 0) {
            LOGGER.error("Declared size {} of chunk {} is negative", (Object)n5, (Object)chunkPos);
            return null;
        }
        return this.createChunkInputStream(chunkPos, by, RegionFile.createStream(byteBuffer, n6));
    }

    private static boolean isExternalStreamChunk(byte by) {
        return (by & 0x80) != 0;
    }

    private static byte getExternalChunkVersion(byte by) {
        return (byte)(by & 0xFFFFFF7F);
    }

    @Nullable
    private DataInputStream createChunkInputStream(ChunkPos chunkPos, byte by, InputStream inputStream) throws IOException {
        RegionFileVersion regionFileVersion = RegionFileVersion.fromId(by);
        if (regionFileVersion == null) {
            LOGGER.error("Chunk {} has invalid chunk stream version {}", (Object)chunkPos, (Object)by);
            return null;
        }
        return new DataInputStream(new BufferedInputStream(regionFileVersion.wrap(inputStream)));
    }

    @Nullable
    private DataInputStream createExternalChunkInputStream(ChunkPos chunkPos, byte by) throws IOException {
        Path path = this.getExternalChunkPath(chunkPos);
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            LOGGER.error("External chunk path {} is not file", (Object)path);
            return null;
        }
        return this.createChunkInputStream(chunkPos, by, Files.newInputStream(path, new OpenOption[0]));
    }

    private static ByteArrayInputStream createStream(ByteBuffer byteBuffer, int n) {
        return new ByteArrayInputStream(byteBuffer.array(), byteBuffer.position(), n);
    }

    private int packSectorOffset(int n, int n2) {
        return n << 8 | n2;
    }

    private static int getNumSectors(int n) {
        return n & 0xFF;
    }

    private static int getSectorNumber(int n) {
        return n >> 8 & 0xFFFFFF;
    }

    private static int sizeToSectors(int n) {
        return (n + 4096 - 1) / 4096;
    }

    public boolean doesChunkExist(ChunkPos chunkPos) {
        int n = this.getOffset(chunkPos);
        if (n == 0) {
            return false;
        }
        int n2 = RegionFile.getSectorNumber(n);
        int n3 = RegionFile.getNumSectors(n);
        ByteBuffer byteBuffer = ByteBuffer.allocate(5);
        try {
            this.file.read(byteBuffer, n2 * 4096);
            byteBuffer.flip();
            if (byteBuffer.remaining() != 5) {
                return false;
            }
            int n4 = byteBuffer.getInt();
            byte by = byteBuffer.get();
            if (RegionFile.isExternalStreamChunk(by)) {
                if (!RegionFileVersion.isValidVersion(RegionFile.getExternalChunkVersion(by))) {
                    return false;
                }
                if (!Files.isRegularFile(this.getExternalChunkPath(chunkPos), new LinkOption[0])) {
                    return false;
                }
            } else {
                if (!RegionFileVersion.isValidVersion(by)) {
                    return false;
                }
                if (n4 == 0) {
                    return false;
                }
                int n5 = n4 - 1;
                if (n5 < 0 || n5 > 4096 * n3) {
                    return false;
                }
            }
        }
        catch (IOException iOException) {
            return false;
        }
        return true;
    }

    public DataOutputStream getChunkDataOutputStream(ChunkPos chunkPos) throws IOException {
        return new DataOutputStream(new BufferedOutputStream(this.version.wrap(new ChunkBuffer(chunkPos))));
    }

    public void flush() throws IOException {
        this.file.force(true);
    }

    protected synchronized void write(ChunkPos chunkPos, ByteBuffer byteBuffer) throws IOException {
        CommitOp commitOp;
        int n;
        int n2 = RegionFile.getOffsetIndex(chunkPos);
        int n3 = this.offsets.get(n2);
        int n4 = RegionFile.getSectorNumber(n3);
        int n5 = RegionFile.getNumSectors(n3);
        int n6 = byteBuffer.remaining();
        int n7 = RegionFile.sizeToSectors(n6);
        if (n7 >= 256) {
            Path path = this.getExternalChunkPath(chunkPos);
            LOGGER.warn("Saving oversized chunk {} ({} bytes} to external file {}", (Object)chunkPos, (Object)n6, (Object)path);
            n7 = 1;
            n = this.usedSectors.allocate(n7);
            commitOp = this.writeToExternalFile(path, byteBuffer);
            ByteBuffer byteBuffer2 = this.createExternalStub();
            this.file.write(byteBuffer2, n * 4096);
        } else {
            n = this.usedSectors.allocate(n7);
            commitOp = () -> Files.deleteIfExists(this.getExternalChunkPath(chunkPos));
            this.file.write(byteBuffer, n * 4096);
        }
        int n8 = (int)(Util.getEpochMillis() / 1000L);
        this.offsets.put(n2, this.packSectorOffset(n, n7));
        this.timestamps.put(n2, n8);
        this.writeHeader();
        commitOp.run();
        if (n4 != 0) {
            this.usedSectors.free(n4, n5);
        }
    }

    private ByteBuffer createExternalStub() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5);
        byteBuffer.putInt(1);
        byteBuffer.put((byte)(this.version.getId() | 0x80));
        byteBuffer.flip();
        return byteBuffer;
    }

    private CommitOp writeToExternalFile(Path path, ByteBuffer byteBuffer) throws IOException {
        Path path2 = Files.createTempFile(this.externalFileDir, "tmp", null, new FileAttribute[0]);
        try (FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.CREATE, StandardOpenOption.WRITE);){
            byteBuffer.position(5);
            fileChannel.write(byteBuffer);
        }
        return () -> Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeHeader() throws IOException {
        this.header.position(0);
        this.file.write(this.header, 0L);
    }

    private int getOffset(ChunkPos chunkPos) {
        return this.offsets.get(RegionFile.getOffsetIndex(chunkPos));
    }

    public boolean hasChunk(ChunkPos chunkPos) {
        return this.getOffset(chunkPos) != 0;
    }

    private static int getOffsetIndex(ChunkPos chunkPos) {
        return chunkPos.getRegionLocalX() + chunkPos.getRegionLocalZ() * 32;
    }

    @Override
    public void close() throws IOException {
        try {
            this.padToFullSector();
        }
        finally {
            try {
                this.file.force(true);
            }
            finally {
                this.file.close();
            }
        }
    }

    private void padToFullSector() throws IOException {
        int n;
        int n2 = (int)this.file.size();
        if (n2 != (n = RegionFile.sizeToSectors(n2) * 4096)) {
            ByteBuffer byteBuffer = PADDING_BUFFER.duplicate();
            byteBuffer.position(0);
            this.file.write(byteBuffer, n - 1);
        }
    }

    static interface CommitOp {
        public void run() throws IOException;
    }

    class ChunkBuffer
    extends ByteArrayOutputStream {
        private final ChunkPos pos;

        public ChunkBuffer(ChunkPos chunkPos) {
            super(8096);
            super.write(0);
            super.write(0);
            super.write(0);
            super.write(0);
            super.write(RegionFile.this.version.getId());
            this.pos = chunkPos;
        }

        @Override
        public void close() throws IOException {
            ByteBuffer byteBuffer = ByteBuffer.wrap(this.buf, 0, this.count);
            byteBuffer.putInt(0, this.count - 5 + 1);
            RegionFile.this.write(this.pos, byteBuffer);
        }
    }

}

