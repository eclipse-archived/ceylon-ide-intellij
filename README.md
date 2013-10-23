IntelliJ Plugin for Ceylon
==========================
# Status
This is an attempt at adding (awesome) support for the Ceylon programming language in IntelliJ IDEA.

# Requirements

This plugin is being written using IntelliJ EAP 13. It will work on both Community and Ultimate editions.
We do not guarantee that this plugin will work with previous versions of IntelliJ (12 and below), as we may use APIs that were introduced in version 13.

Only a subset of the Ceylon SDK (0.6.1) is embedded within the plugin, mainly to parse and highlight errors in `.ceylon` files. If you intend to compile/run a Ceylon project, you will also need to download the latest Ceylon SDK (currently 0.6.1).

# Features

While we are working very hard at making this a great plugin, only a few features are working at the moment:

- `.ceylon` files recognition
- parsing and syntax highlighting
- project make / file compilation
- running a top-level method
- nifty lang features (code commenting, braces/quotes matching, code folding, structure viewing)

Other features will come soon!

# Hacking

If you want to start hacking on this plugin, you will need:

- a Community or Ultimate version of [IntelliJ EAP 13](http://confluence.jetbrains.com/display/IDEADEV/IDEA+13+EAP)
- the following plugins: "Plugin DevKit", "UI Designer" + "UI Designer (core)", "PsiViewer" (optional but recommended)
- a clone of https://github.com/JetBrains/intellij-community/ is highly recommended since you will likely have to debug code from the IntelliJ platform

We tried to reduce as much as possible the number of external dependencies, the plugin should be self-sufficient.