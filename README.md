# gitometer

Tool for modification rate analysis in git repository. 
Actually, just an MVP at the moment.

## Exec script
Example:
```
./bin/exec.sh -o target/gitometer-output -r ~/tmp/src-jgit/.git 
```
* `-o` parameter is path for output. Such directory (or file) should not exist.
* `-r` parameter is path to git repository to analyse. Pay attention to `.git` part of the path. 
       This parameter should point to either bare repository or `.git` directory.
* `-c` to rebuild project (`mvn clean package`).

## Issues
* binary files are ignored.
* merge commits are ignored.
* result is incorrect for moved files. 
  Ex. repository [jgit-cookbook](https://github.com/centic9/jgit-cookbook), 
  revision `1ad10d8`, incorrect result for file `src/main/java/org/dstadler/jgit/porcelain/CloneRemoteRepository.java`.

## Design flaws
* In classes `Blob`, `GitObjectStats`, `Tree` method `equals` is implemented, but `hashCode` is not implemented.
* Usage of `instanceof` in `ThymeleafView` class.
* `ThymeleafView` knows about both directories heirarchy and rendering details. Looks like SRP violation.
* No unit tests for `ThymeleafView`.
* Hack in `JgitCommitSource::getSubmoduleIds`.
* No unit tests for `JgitCommitSource`.
* assertNotEquals should be used instead of assertFalse.