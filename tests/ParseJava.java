import com.sun.source.util.JavacTask;
import com.sun.source.tree.CompilationUnitTree;
import java.util.Arrays;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/** Parse only: no Android symbol resolution, bytecode generation or device execution. */
class ParseJava {
    public static void main(String[] args) throws Exception {
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
        if(compiler==null)throw new IllegalStateException("A full JDK 17 or newer is required");
        DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();
        int units=0;
        try(StandardJavaFileManager files=compiler.getStandardFileManager(diagnostics,null,null)){
            JavacTask task=(JavacTask)compiler.getTask(null,files,diagnostics,
                List.of("-proc:none","--release","17"),null,files.getJavaFileObjectsFromStrings(Arrays.asList(args)));
            for(CompilationUnitTree ignored:task.parse())units++;
        }
        boolean error=false;
        for(Diagnostic<? extends JavaFileObject> d:diagnostics.getDiagnostics())if(d.getKind()==Diagnostic.Kind.ERROR){
            System.err.println(d);error=true;
        }
        if(error)System.exit(1);
        System.out.println("{\"unitsParsed\":"+units+",\"errors\":[],\"scope\":\"Java 17 syntax only; no Android type checking, compilation or execution\"}");
    }
}
