# baremetal4j
A java aspect for allowing debugging at the byte code level from source debuggers (as in IDEs)

A work in progress, not ready for use as of yet.

This java agent rewrites classes on load so that you can step through methods at the byte code level. 
It generates 'source' files that resemble javap output, to use as source.

To use, do

    java -javaagent:/path/to/baremetal4j-0.2.0.jar=apply_all=true;source_path=/tmp/bm

Where the options you can pass are

    apply_all=(true|false) - whether to apply all classes loaded or not
    
    inclusion_pattern={a regex pattern} - a pattern to choose fully qualified class names to include
    
    exclusion_pattern={a regex pattern} - a pattern to choose fully qualified class names to reject
    
    source_path={dirName} - where to write the 'source' files.
    
    
(if apply_all is set to true, the inclusion patterns are ignored)
