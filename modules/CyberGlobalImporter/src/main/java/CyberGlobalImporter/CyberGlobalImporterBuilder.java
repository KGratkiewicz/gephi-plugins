package CyberGlobalImporter;

import org.gephi.io.importer.api.FileType;
import org.gephi.io.importer.spi.FileImporter;
import org.gephi.io.importer.spi.FileImporterBuilder;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = FileImporterBuilder.class)
public class CyberGlobalImporterBuilder implements FileImporterBuilder {

    public static final String IDENTIFER = "json";

    @Override
    public FileImporter buildImporter() {
        return new CyberGlobalImporter();
    }

    @Override
    public String getName() {
        return IDENTIFER;
    }

    @Override
    public FileType[] getFileTypes() {
        FileType json = new FileType(".json", "JSON format");
        FileType tsf = new FileType(".tsf", "TSF format");
        return new FileType[]{json, tsf};
    }

    @Override
    public boolean isMatchingImporter(FileObject fileObject) {
        return fileObject.getExt().equalsIgnoreCase(IDENTIFER);
    }
}