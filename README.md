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

# Installation

For the moment, we do not provide an official release package in the JetBrains plugins repository,
since it is still under development. However, installable archives of the current development version
are generated and made available in an alternate plugin repository, on a regular basis (see below).
You can either install the Ceylon IDE for IntelliJ from there, or build it from sources.

## Common prerequisites

The following software must be installed:

- [IntelliJ 2016.1](http://www.jetbrains.com/idea/download/) Community or Ultimate
- [JDK 7 or 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

## Install from the Development plugin repository

_This is the simplest way to install Ceyon IDE for IntelliJ._

Inside the Intellij IDEA environment, follow
[these instructions](https://www.jetbrains.com/idea/help/managing-enterprise-plugin-repositories.html)
to add one of the following custom repository URLs. From the repository, you will be able to install
a plugin named 'Ceylon IDE'. After installing the plugin and restarting IntelliJ IDEA, you will
have the ability to create Ceylon IntelliJ modules in which you can develop and run Ceylon code.


#### For IntelliJ 15/2016.1+ and Android Studio 2.0+

Development builds are published irregularly to this repository:

<http://downloads.ceylon-lang.org/ide/intellij/development/updatePlugins.xml>


Nightly builds are published here:

<https://ci-ceylon.rhcloud.com/job/ceylon-ide-intellij/ws/out/installation-packages/updatePlugins.xml>


#### For IntelliJ 14 and Android Studio 1.5:

Development builds are published irregularly to this repository:

<http://downloads.ceylon-lang.org/ide/intellij/development/legacy/updatePlugins.xml>


## Build and install from sources

1. Clone [`ceylon`](http://github.com/ceylon/ceylon) and other required dependencies from Git:

       git clone https://github.com/ceylon/ceylon.git
       cd ceylon
       ant setup-sdk setup-ide

   This will clone sibling projects including `../ceylon-sdk`, `../ceylon-ide-common`, and
   `../ceylon-ide-intellij`.

2. Edit `../ceylon-ide-intellij/plugin-ceylon-code/build.properties` and change the following line
   so that it points to your IntelliJ installation:

       ideaRoot=/Applications/IntelliJ IDEA 2016.1 CE.app/Contents/

3. Build the Ceylon distribution, SDK, and Ceylon IDE:

       ant dist sdk intellij

4. Inside IntelliJ, go to 'Preferences ... > Plugins' and click 'Install plugin from disk...',
   select `../ceylon-ide-intellij/out/installation-packages/CeylonIDEA.zip`.

5. Enjoy coding Ceylon in IntelliJ!

## Hack the IDE

If you want to debug or hack this plugin, you'll need to build it from sources.

1. Make sure you're using IntelliJ 2016.1 (support for IntelliJ 14/15 and Android Studio is
   planned in a [separate branch](https://github.com/ceylon/ceylon-ide-intellij/tree/141.x-compat))
   A clone of `https://github.com/JetBrains/intellij-community` is highly recommended for hacking
   since you will likely have to debug code from the IntelliJ platform

2. Go to 'Preferences ... > Plugins', and ensure that the following plugins are installed and
   enabled in your IDEA instance:
    - Plugin DevKit,
    - UI Designer,
    - PsiViewer (optional but recommended)

3. Clone [`ceylon`](http://github.com/ceylon/ceylon) and other required dependencies from Git:

       git clone https://github.com/ceylon/ceylon.git
       cd ceylon
       ant setup-sdk setup-ide

   This will clone sibling projects including `../ceylon-sdk`, `../ceylon-ide-common`, and
   `../ceylon-ide-intellij`.

4. Edit `../ceylon-ide-intellij/plugin-ceylon-code/build.properties` and change the following line
   so that it points to your IntelliJ installation:

       ideaRoot=/Applications/IntelliJ IDEA 2016.1 CE.app/Contents/

5. Build the Ceylon distribution, SDK, and Ceylon IDE. In the directory `../ceylon`, type:

       ant dist sdk intellij

   This will create a `dist` sub-directory in `../ceylon/dist`, with a full Ceylon distribution,
   along with everything needed to build Ceylon IDE, including the Ceylon SDK, formatter, Java to
   Ceylon converter, `ceylon-ide-common`, and Ceylon IDE itself. (See
   [these more detailed instructions](https://github.com/ceylon/ceylon-dist/blob/master/README.md#building-the-distribution)
   if necessary.)

6. Open the project `ceylon-ide-intellij` in IDEA. You might be requested to enter the value of two
   path variables. Go to 'Preferences ... > Appearance & Behavior > Path Variables', and add a
   variable named `CEYLON_DIST` pointing to `../ceylon/dist/dist`.

7. In the IDE's 'Preferences ... > Editor > File Types', under 'Recognized File Types', register
   `*.car` as an extension under the 'Archive' file type.

8. Go to 'File > Project Structure ... > SDKs':

    - Click on the `+` icon and add a new 'JDK' pointing to a *Java SDK 1.8*
    - Click on the `+` icon and add a new 'IntelliJ Platform Plugin SDK' pointing to the location
      of your IntelliJ installation (the correct folder should be preselected)

   Now, in the 'File > Project Structure ... > Project' section:

     - Set the 'Project SDK' to the previously created IntelliJ plugin SDK
     - Set the 'Project language level' to 7.0
     - Set the 'Project compiler output' to the `./out` directory

   Apply these changes and close the 'Project Structure' dialog.

9. In 'Run > Edit configurations', create a new run configuration with type 'Plugin' and leave the
    default options. Run this configuration to test the IDE.


### Edit Ceylon code from Eclipse

It is possible to edit the Ceylon part of this plugin, which is located in `plugin-ceylon-code`, in
Eclipse. From Eclipse, do the usual 'File > Import > Existing Projects into Workspace' and make it
point to `ceylon-ide-intellij/plugin-ceylon-code`. The project should already be correctly configured.
