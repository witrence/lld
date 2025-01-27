import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

interface Criteria {
    /**
     * @return true if File matches this Criteria else false.
     */
    boolean matches(File file);

    default Criteria and(Criteria other) {
        return new AndCriteria(this, other);
    }

    default Criteria or(Criteria other) {
        return new OrCriteria(this, other);
    }

}

class AndCriteria implements Criteria {
    private final Criteria a;
    private final Criteria b;

    AndCriteria(Criteria a, Criteria b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean matches(File file) {
        return a.matches(file) && b.matches(file);
    }
}

class OrCriteria implements Criteria {
    private final Criteria a;
    private final Criteria b;

    OrCriteria(Criteria a, Criteria b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean matches(File file) {
        return a.matches(file) || b.matches(file);
    }
}

class MatchByName implements Criteria {

    private final String name;

    MatchByName(String name) {
        this.name = name;
    }

    @Override
    public boolean matches(File file) {
        return file.getName().equals(name);
    }
}

class MatchBySize implements Criteria {

    private final long sizeInBytes;
    private boolean greater;

    MatchBySize(long sizeInBytes, boolean greater) {
        this.sizeInBytes = sizeInBytes;
        this.greater = greater;
    }

    @Override
    public boolean matches(File file) {
        if(greater){
            return file.getTotalSpace() > sizeInBytes;
        } else{
            return file.getTotalSpace() < sizeInBytes;
        }
    }
}
// name AND (size OR extension)
public class Find {
    
    
    public static void main(String[] args) {
        Criteria c = new MatchByName("witrence").and((new MatchBySize(100, true).or(new MatchByName("k"))));
        List<File> results = new Find().find("/", c);
    }

    public List<File> find(String directoryPath, Criteria criteria) {

        // nested


        List<File> result = new ArrayList<>();
        try {

        Files.list(Paths.get(directoryPath)).map(Path::toFile).forEach(f ->  {
            if(criteria.matches(f)) {
                result.add(f);
            }
        });
        } catch(IOException e) {
            
        }
        return result;

    }

    
}
// class FindPatterns {

// }

/*
1. Ability to find by various parameters like name, size, extension.
2. Should be extensible to be able to add ability to search on more params in the future.
3. Should be able to search in directories (or nested directories).

File System API is provided.

Info Required - 
1. Search path
2. Search Function

Files Metadata Temporary Storage Format - 
In form of a array of object where each object contains keys - name, extension, size. 

Results Format - 
1. File path
2. Size
3. Extension




 */



