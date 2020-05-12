# gitometer

## Design flaws
* In classes `Blob`, `GitObjectStats`, `Tree` method `equals` is implemented, but `hashCode` is not implemented;
* Usage of `instanceof` in `ThymeleafView` class;
* `ThymeleafView` knows about both directories heirarchy and rendering details. Looks like SRP violation.
* No unit tests for `ThymeleafView`.
* Hack in `JgitCommitSource::getSubmoduleIds`
* No unit tests for `JgitCommitSource`