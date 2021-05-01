# **Omega IDE**

![](/images/light.png)

![](/images/dark.png)


⏩ Omega IDE is a blazing fast Java IDE promising performance even on minimal resource environment with a least available RAM of 210 MB. 

Here is a proof, 

OS - Deepin Linux

RAM - 2GB

Storage - 320 GB

GPU - Intel Integrated

CPU - Intel Pentium Duo 2 cores 2 logical processors 2 x 2.20 GHz

![](/images/p1.png)

![](/images/p2.png)


Omega IDE when editing its own source code uses at most 150 MB of RAM (only when used extensively) 
so you can imagine how good it is for small projects 🤩.

Omega IDE has a very small download size.

It has a beautiful text editor with code folding (thanks to rsyntaxtextarea).

Omega IDE can also be used as a **Instant IDE for any** other **programming language** as well.

It tries to reassemble a material look so that it is easy to adapt the customized UI.

It has a elegant UI filled with custom components(have a look at sources at src/omega/comp).

**The component library of Omega IDE is independent of any other IDE component. **

**As a result You can use this component library under the terms of GNU GPL v3**

Want to have a look before giving a try?

See whats new in the latest release on the official channel!

[![](/images/youtube-icon.png)](https://www.youtube.com/channel/UCpuQLV8MfuHaWHYSq-PRFXg)



# Installing

Dependency **JDK 16** or above

Just download the **Omega IDE.jar** file (or .deb installer if you are on debian) from the release section and run.

# Please Read -- For Java Projects Only

After opening a project which was not created with Omega IDE don't forget to select the JDK from Preferences first else the tools may not work as expected.

![](/images/java-settings.png)

All the algorithms of Omega IDE rely on Java Conventions.

As a result the IDE will not work as intended if you miss so.

You cannot create a class without a package, this means that using default packages

is prohibited in Omega IDE.

Also when creating a source file you have specify the full-qualified name.

Like this,

![](/images/file-wizard.png)


# Creating a Project

On a fresh install, the IDE will ask you to point to a directory to be used as a workspace.

Then, you will be greeted with a launcher window.

There are separate project wizard for Java and Non-Java Projects.

![](/images/launcher.png)

## Java

From there click **New Project** with tag *Project*, now a project wizard will appear.

You should see a look like this.

![](/images/java-wizard.png)

Now Enter the project name.

The colon near the top most field is a component containing the path of workspace directory.

You can change it whenever you want.


Now below the project name field you will see a non-editable field

with text "Choose Java Development Kit", just right next to this field you will see a colon component again.

Click that component and it will ask you to specify a directory containing the JVMs

so that the IDE can identify which JDKs are available.

On Linux, the path is usually **/usr/lib/jvm**

On Windows, the path is usually like **C:\Program Files\Java** or **C:\Program Files(x86)\Java** based on the architecture.

Just type in the right path in the file chooser dialog or navigate to the directory if you dont know!

After this the wizard will ask to select the available JDKs, select One.


Now you will a text area below labeled as **Source Files**.

It serves a major role when you want you have multiple source files to create or just want to use it.

What it actually does is that it creates the source files specified as soon as the project wizard prepares the project.

write there,

main.Tutorial -class

main.Demo -interface


Now leaving the dependency part.

Click create.

Now you can see that your newly created project is ready for editing.

The IDE has created four default directories(listed on the FileTree on left),

and it has also created the two source files Tutorial(class) and Demo(interface),

and they are ready for editing right in the main tab panel.

## Any Programming Language

If already editing a project, click **File** Menu from the Top.

Now Click the **New Project (non-project)**.

If still in the launcher, click **New Project** with tag *Non-Java*.

A simple Project Wizard will appear,

asking you to specify the Project Name and to select workspace if needed.

![](/images/non-java-wizard.png)

But Here, this project wizard works different.

You see a text area below the project name field.

That text area is demanding directory names separated by a new line character.

For example: 

Specify the project name as Demo Python Project

In the text area write

```
src
docs
lib
```
On Clicking **Create**, you will see your project ready and opened in the IDE.

# Setting Up a Project

## Java

For Java Type Projects, there is a combined management tool for managing class-path & module-path.

You can open it by clicking the **Project** Menu and by clicking **Manage Class-path** or **Manage Module-path**.

![](/images/dependency-manager.png)


## Any Programming Language

Ok! Speaking Straightforward, there is no gui tool to manage dependencies for other programming languages.

But wait, you can set this up by yourself from the settings window.

There is a different Settings Window for Non-Java Projects.

Just Open Preferences, there you will see two fields labelled compile-time and run-time.

![](/images/non-java-settings.png)

Fill in the compile-time and the run-time command(s) and don't forget to select the working directory.

*If there is no compile-time argument in your project then you can leave the compile-time field empty*

There is one more field component below.

This component can be used to make a list of paths of source files before compilation.

Like the one we need in compiling a groovy project.

The first field is asking the type of file and the second one is asking the file name in which the paths will be written.
The **"** component is for surrounding the paths within double-quotes if you want.

Don't forget to Click Apply else you will lose changes.

# Running a Project

If any editor is opened, then by hitting CTRL + SHIFT + R launches the Project.

Else click the Red Colored Run button in the ToolMenu.

Right Clicking the Run Button launches the Project without Build.

# Contributing

Well, I recommend using Omega IDE for editing its own sources for a smoother experience.

The **lib** folder contains the class-path dependenies.

The **res** is the resource-root.

# Note

Omega IDE is not yet totally evolved which means it has no support for any version control system yet and bugs are likely to come.

# Small Gallery

![](/images/g1.png)
![](/images/g2.png)
![](/images/g3.png)
![](/images/g4.png)
![](/images/g5.png)
![](/images/g6.png)
![](/images/g7.png)


# Hope You Like It
instagram @i_am_arham_92

Want to Donate for the Project?

**Google Pay** : UPI ID 1 -> **arhamfar22@okaxis**

**Google Pay** : UPI ID 1 -> **arhamfar22@okicici**

**QR Code**

![](/images/qr_code.png)






















