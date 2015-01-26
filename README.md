IntelliJ Plugin for Ceylon
==========================
# Status
This is an attempt at adding (awesome) support for the Ceylon programming language in IntelliJ IDEA.

# Requirements

This plugin is being written using IntelliJ 14. It will work on both Community and Ultimate editions.
We do not guarantee that this plugin will work with previous versions of IntelliJ (13 and below), as we may use APIs that were introduced in version 14.

We embed most of the raw Ceylon installation in the plugin, so you won't necessarily have to download Ceylon separately. You may need a local repository though.

# Features

While we are working very hard at making this a great plugin, only a few features are working at the moment:

- `.ceylon` files recognition
- parsing and syntax highlighting
- project make / file compilation
- running a top-level method/class
- nifty lang features (code commenting, braces/quotes matching, code folding, structure viewing)
- documentation pop-ups
- basic Add Ceylon module/file functionality
- goto class/interface (Ctrl-N)
- identifiers work as references, which enables Ctrl-click navigation, rename refactoring, and usages search

This makes of it a useful tool for browsing existing Ceylon projects, and experimenting with writing and running
simple Ceylon programs. To be used for more serious development, many more features are needed, notably:

- Code Completion (issue #26)
- More dynamic typechecking (eg. include newly added file in typechecking correctly, use external libraries etc.)


# Testing & Hacking

For the moment, we do not provide an official release package in the Jetbrains plugins repository, since it is still under early development.
However, installable zips of the current development version are generated and will be made availabe in an alternate plugin repository, on a regular basis.
You can either install the CeylonIDEA from there, or build it from sources.

## Common pre-requisites

- a _Community_ or _Ultimate_ version of **[IntelliJ 14](http://www.jetbrains.com/idea/download/)**

## Install from the Development plugin repository

_This is the **simplest way** to install the Ceyon IDEA IntelliJ plugin._ 

Inside the Intellij IDEA environment, follow the instructions [there](https://www.jetbrains.com/idea/help/managing-enterprise-plugin-repositories.html) in order to add the following URL :
```
http://downloads.ceylon-lang.org/ide/intellij/development/updatePlugins.xml
```
to the list of custom plugin repositories.

Then, from this new repository, you should be able to install those 2 plugins at the same time :
- **CeylonIDEA**, the Ceylon development environment for IntelliJ IDEA
- **CeylonRuntime**, a required plugin that allows running Ceylon code inside an IntelliJ plugin. It is required by the CeylonIDEA plugin since CeylonIDEA will have parts developped in Ceylon. 

After installing both plugins and restarting IntelliJ IDEA, you should have the ability to create Ceylon IntelliJ modules in which you can develop and run Ceylon code.

## Install from sources 

If you want to use the current version, or debug/hack this plugin, you will need to build it from sources :

1. Make sure you are using **IntelliJ 14** (it won't work with the current stable version 12.x, and might not work with version 13.x)

2. A clone of https://github.com/JetBrains/intellij-community is highly recommended for hacking since you will likely have to debug code from the IntelliJ platform

3. Ensure that the following IntelliJ plugins are installed and enabled in your IDEA instance:
    - **Plugin DevKit**, 
    - **UI Designer** + **UI Designer (core)**, 
    - **PsiViewer** (optional but recommended)

4. Setup a clone of https://github.com/ceylon/ceylon-ide-intellij (the main plugin project)

5. Make sure that the following GitHub repositories have all been cloned locally into the same parent directory :
    - https://github.com/ceylon/ceylon-dist (the Ceylon distribution project)
    - https://github.com/ceylon/ceylon-ide-common (shared between the Eclipse plugin and the IntelliJ plugin)

6. Setup all the Ceylon distribution sibling projects by running the `ant setup` command in direcrory `../ceylon-dist`. This will create sibling projects such as : `../ceylon-spec`, `../ceylon-compiler`, etc ...
	
7. Build a full Ceylon distribution locally (see [here](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution) for more details) :
    - In the `../ceylon-dist` directory run : `ant clean publish-all ide-quick`
    - This will create a `dist` sub-directory in `../ceylon-dist`, with the built Ceylon distribution.

8. Open the project `ceylon-ide-intellij` in IDEA. You might be requested to enter the value of 2 path variables (see next point).

9. In `Settings > Build, Execution, Deployment > Path Variable`, you should add 2 path variables :
    - `̀CEYLON_DIST_LIB` should point to `../ceylon-dist/dist/lib`
    - `̀CEYLON_DIST_REPO` should point to `../ceylon-dist/dist/repo`

10. In the IDE's `Preferences > File Types`, under `Recognized File Types`, register `*.car` as `Archive files`

11. Go to `File > Project Structure > SDKs`

12. Click on the `+` icon and add a new `JDK` pointing to a *Java SDK 1.7*

13. Click on the '+' icon and add a new `IntelliJ Platform Plugin SDK` pointing to where IntelliJ is installed (the correct folder should be preselected)

14. In the `Project` part, set the `Project SDK` to the previously created IntelliJ plugin SDK

15. Set the `Project language level` to 7.0

16. Set the `Project compiler output` to the `./out` directory

17. Apply changes and close the settings dialog

18. In `Run > Edit configurations`, create a new run configuration with type `Plugin` and leave the default options

19. Run this configuration and enjoy writing Ceylon in IntelliJ!
