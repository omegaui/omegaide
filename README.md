<p align="center">
  <img width="140" src="https://raw.githubusercontent.com/omegaui/omegaide/main/res/omega_ide_icon128.png" />  
  <h2 align="center">Omega IDE</h2>
  <p align="center">The Blazing Fast Java IDE and an Instant IDE for any programming language</p>
  <p align="center">For the Low End Computers</p>
</p>
<p align="center">
  <a href="https://github.com/omegaui/omegaide/issues">
    <img src="https://img.shields.io/github/issues/omegaui/omegaide"/> 
  </a>
  <a href="https://github.com/omegaui/omegaide/network/members">
    <img src="https://img.shields.io/github/forks/omegaui/omegaide"/> 
  </a>  
  <a href="https://github.com/omegaui/omegaide/stargazers">
    <img src="https://img.shields.io/github/stars/omegaui/omegaide"/> 
    <a href="https://github.com/omegaui/omegaide/LICENSE">
  </a>
    <img src="https://img.shields.io/github/license/omegaui/omegaide"/> 
  </a>
</p>

<p align="center">
  <a href="https://github.com/omegaui/omegaide/blob/main/Donate.md">
    <h1>do { nate }</h1>
  </a>
</p>

**Omega IDE v2.2 - The Awesome Beta**

![](/res/light.png)

![](/res/dark.png)


**Omega IDE v2.1 - Latest Stable**

![](/images/light.png)

![](/images/dark.png)


# Written from Scratch
Omega IDE has integrated support for the **java** programming language at its heart.

What makes Omega IDE a unique Java IDE is that it can build huge java projects **instantly**.

Don't believe me? Try it yourself 

- Open your pure java project(should not be governed by any build systems like gradle, maven, etc) in omegaide or create one
- Build your project
- Edit some code
- Hit the rocket button to see instant results.

It has a beautiful text editor with code folding, thanks to [rsyntaxtextarea](https://github.com/bobbylight/RSyntaxTextArea).

The Default Swing Components use the [FlatLaf Layer](https://www.formdev.com/flatlaf)

It tries to reassemble a material look so that it is easy to adapt the highly customized UI.

The credit of having meaningful and beautiful icons goes to [icons8.com](https://icons8.com)

*It has a elegant UI filled with custom components(have a look at sources at src/omega/comp for current stable or at src/omegaui for current beta).*

*The component library of Omega IDE is independent of any other IDE component.*

*As a result You can use this component library under the terms of GNU GPL v3.*

# When We Need It?
Development on a low end PC is slow and annoying if your *great* IDE doesn't performs well on it or it just can't run on it.
But what if you can utilize your own OS for blazing fast and smooth workflow.

Shell Scripts are the fastest readable codes that run in the Operating System's shell, thus, omegaide can utilize shell scripts 
(instead of using pre-defined build codes) to
build your projects and make that low end PC fit for development purpose thus,
making omegaide an instant IDE for any programming language.

By Using Omega IDE you can avoid

- Installing Different IDEs that **cannot** perform well on your PC.

- Switching through different terminal windows and the text editor to compile, spot the error, correct it and again go back, 
recompile and .... to be continued.

and can

- Just Build your project by pressing the shortcut.

# How it is different from VS Code, Atom, etc?
- omegaide provides in-editor key bindings to build and run projects.

- Not only this, whenever you run your build script it highlights any error in the editor itself which comes so that
you don't even need not to look at your `build` script's output to search for any error that comes, 
(currently supports highlights for Kotlin, C, C#, C++, Python).

- You just need to click on the `run` button and the IDE auto executes your `run` script if the `build` script executes successfully.

- Uses a lot less RAM than any other equivalent text editor when used as an instant IDE.

- In-Editor minimal code completion (can be taken to next level by creating a plugin).

# Run
If you want to get early access to optimizations and new features you can freely use latest beta available.

[Check out the dailybuild(.jar) now](https://raw.githubusercontent.com/omegaui/omegaide/main/out/Omega%20IDE-dailybuild.jar).

**Available installation formats**

**.jar** Portable Java Archive (for both stable and beta versions)

**.deb** Debian Setup (available only for the current stable version)

# Build From Source
`JDK >= 17`
the `lib` directory already contains classpath dependencies.

just clone this repo, open the folder `omegaide` in Omega IDE and hit `Run`.

# See It in Action!

[Here at the official Github Page](https://omegaui.github.io/omegaide)

# Remember
A [wiki](https://github.com/omegaui/omegaide/wiki/Omega-IDE-Wiki-Home-Page) is always useful.

# Show your support
[Donate](https://github.com/omegaui/omegaide/blob/main/Donate.md)