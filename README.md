Ceylon IDE for IntelliJ [![Join the chat at https://gitter.im/ceylon/ceylon-ide-intellij](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/ceylon/ceylon-ide-intellij) [![Build Status](https://ci-ceylon.rhcloud.com/buildStatus/icon?job=ceylon.formatter)](https://ci-ceylon.rhcloud.com/job/ceylon.formatter)
=======================

This project is an IntelliJ IDEA-based IDE for the Ceylon programming language. It's not yet released,
but it's already very usable.

# Requirements

This plugin is being written using IntelliJ 2016.1. It will work on both Community and Ultimate editions.

We have a [dedicated branch](https://github.com/ceylon/ceylon-ide-intellij/tree/141.x-compat) for
older versions of IntelliJ (IDEA 14 *and* Android Studio 1.5.1 which is based on IntelliJ 14.1).
Please note that these older versions of the IntelliJ platform have slightly different APIs, which
means a few things may not work as expected. In particular, they contain
[a bug that is critical](https://youtrack.jetbrains.com/issue/IDEA-132606) to the correct loading of
Ceylon classes, which has been fixed in IntelliJ 2016.1. We'll try to backport a fix on this branch
for better user experience.

We embed a full Ceylon distribution in the plugin, so you won't necessarily have to download Ceylon
separately. You may need a local repository though.

# Features

While we are working very hard at making this a great plugin, only a few features are working at the
moment. Please note that the plugin is not currently ready for daily usage.

See the [Features](https://github.com/ceylon/ceylon-ide-intellij/wiki/Features) page for a detailed
list of things that are working and that will be implemented in the future.

# Testing & Hacking

For the moment, we do not provide an official release package in the JetBrains plugins repository,
since it is still under development. However, installable archives of the current development version
are generated and made available in an alternate plugin repository, on a regular basis (see below).
You can either install the Ceylon IDE for IntelliJ from there, or build it from sources.

## Common pre-requisites

- a _Community_ or _Ultimate_ version of **[IntelliJ 2016.1](http://www.jetbrains.com/idea/download/)**
- **OSX users**: this plugin needs a JRE 7+, but IntelliJ 14 requires a JDK 6 by default. To run
  Ceylon IDE, you can either:
  - download IntelliJ with a [custom JDK bundled](https://confluence.jetbrains.com/display/IntelliJIDEA/Previous+IntelliJ+IDEA+Releases)
  - download the standard version, install it, then modify `Info.plist` to
    [force a recent version of Java](https://intellij-support.jetbrains.com/hc/en-us/articles/206827547-Selecting-the-JDK-version-the-IDE-will-run-under)

## Install from the Development plugin repository

_This is the **simplest way** to install Ceyon IDE for IntelliJ._ 

Inside the Intellij IDEA environment, follow
[these instructions](https://www.jetbrains.com/idea/help/managing-enterprise-plugin-repositories.html)
in order to add one of the following URLs:

**IntelliJ 15/2016.1+ and Android Studio 2.0+:**

Development builds (updated from time to time):
```
http://downloads.ceylon-lang.org/ide/intellij/development/updatePlugins.xml
```

Nightly builds:
```
https://ci-ceylon.rhcloud.com/job/ceylon-ide-intellij/ws/out/installation-packages/updatePlugins.xml
```

**IntelliJ 14 and Android Studio 1.5:**
```
http://downloads.ceylon-lang.org/ide/intellij/development/legacy/updatePlugins.xml
```

to the list of custom plugin repositories.

Then, from this new repository, you should be able to install a plugin named **Ceylon IDE**. After
installing the plugin and restarting IntelliJ IDEA, you should have the ability to create Ceylon
IntelliJ modules in which you can develop and run Ceylon code.

## Install from sources 

If you want to use the current version, or debug/hack this plugin, you will need to build it from
sources:

1. Make sure you're using **IntelliJ 2016.1** (support for IntelliJ 14/15 and Android Studio is
   planned in a [separate branch](https://github.com/ceylon/ceylon-ide-intellij/tree/141.x-compat))

2. A clone of `https://github.com/JetBrains/intellij-community` is highly recommended for hacking
   since you will likely have to debug code from the IntelliJ platform

3. Ensure that the following IntelliJ plugins are installed and enabled in your IDEA instance:
    - **Plugin DevKit**, 
    - **UI Designer**, 
    - **PsiViewer** (optional but recommended)

4. Set up a clone of [`ceylon`](http://github.com/ceylon/ceylon) and all the other required dependencies:
    - `mkdir ceylon && cd ceylon`
    - `git clone https://github.com/ceylon/ceylon`
    - `cd ceylon`
    - `ant setup-sdk setup-ide` (this will create sibling projects such as: `../ceylon-sdk`, `../ceylon-ide-common`, etc)

5. Edit `../ceylon-ide-intellij/plugin-ceylon-code/build.properties` and change the following line
   so that it points to your IntelliJ installation:
    - `ideaRoot=/Applications/IntelliJ IDEA 2016.1 CE.app/Contents/`

7. Build a full Ceylon distribution locally by running `ant clean-all dist sdk intellij` in the
    `../ceylon` directory. This will create a `dist` sub-directory in `../ceylon/dist`, with a local
   Ceylon distribution, along with everything needed to build Ceylon IDE, including the Ceylon SDK,
   formatter, Java to Ceylon converter, `ceylon-ide-common`, and Ceylon IDE itself. (See
   [these more detailed instructions](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution)
   if necessary.)

8. Open the project `ceylon-ide-intellij` in IDEA. You might be requested to enter the value of two
   path variables. Go to 'Preferences ... > Appearance & Behavior > Path Variables`, and add a
   variable named `CEYLON_DIST` pointing to `../ceylon/dist/dist`.

9. In the IDE's 'Preferences ... > Editor > File Types', under 'Recognized File Types', register
   `*.car` as an extension under the 'Archive' files type.

11. Go to 'File > Project Structure ... > SDKs':
    - Click on the `+` icon and add a new 'JDK' pointing to a *Java SDK 1.8*
    - Click on the `+` icon and add a new 'IntelliJ Platform Plugin SDK' pointing to the location
      of your IntelliJ installation (the correct folder should be preselected)

12. Now, in the 'File > Project Structure ... > Project' section:
     - Set the 'Project SDK' to the previously created IntelliJ plugin SDK
     - Set the 'Project language level' to 7.0
     - Set the 'Project compiler output' to the `./out` directory

13. Apply changes and close the 'Project Structure' dialog.

14. In 'Run > Edit configurations', create a new run configuration with type 'Plugin' and leave the
    default options.

15. Run this configuration and enjoy writing Ceylon in IntelliJ!

### Edit Ceylon code from Eclipse

It is possible to edit the Ceylon part of this plugin, which is located in `plugin-ceylon-code`, in
Eclipse. From Eclipse, do the usual 'File > Import > Existing Projects into Workspace' and make it
point to `ceylon-ide-intellij/plugin-ceylon-code`. The project should already be correctly configured.
