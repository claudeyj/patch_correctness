# Generate Bytecode Level PraPR Patches
Following instructions in PraPR website: https://github.com/prapr/prapr we can generate bytecode level PraPR patches.
For each programs in Defects4j, configure the program with maven PraPR plugin in `pom.xml` file and then run maven command in command line: 
```
mvn org.mudebug:prapr-plugin:prapr -Dhttps.protocols=TLSv1.2
```
After that we can get all bytecode plausible patches. We focus on two output of PraPR: `pool' directory which contains `mutant-*.class`, i.e., the bytecode patch files, and the 'fix-report.log' file. We move all these results into a directory, e.g., prapr_bin_patches_1.2 and prapr_bin_patches_2.0. 

# Transform Bytecode Level PraPR Patches to Source Code Level PraPR Patches
We use JD-Core API as our decompiler, see https://github.com/java-decompiler/jd-core. Say we have patch called `mutant-1.class`, corresponding to `example.java`. The bytecode file before mutation is `example.class`. We decompile `example.class` and `mutant-1.class`, the results are noted as `a-example.java` and `b-example.java` respectively. This step is wrapped in `decompile_with_jd.py`.

Then we use linux unidiff command to get difference of `a-*.java` and `b-*.java`, the result is denoted as project-version-mutant-id.patch. The corresponding script in `diff_jd.py`. We apply the result patch to original java files to get candidate source level patched java files. Then we verify this patches by replacing patched java files with their corresponding original java files and run `defects4j compile` and `defects4j test` command. If the patched program can't compile, we will manually fix the patch (fix-report.log tells us the mutation operation and the mutation position). Patches can't be fixed will be excluded.

Finally for all the fixed patches we run linux diff command to get genuine source-code level patches, the script is shown in `apply_patch.py`
