# Project PARMOREL

Personalized and Automatic Repair MOdels using REinforcement Learning (PARMOREL).

PARMOREL is a WIP tool for automatic software models repairing using reinforcement learning (Q-Learning at the moment).
The code in Main.java corresponds to an evaluation in where we fix 100 mutant errors produced with AMOR Ecore mutator tool (https://bit.ly/2Sse4kU). All mutants are inside "mutants" folder. Fixed models are generated in "fixed" folder. It is necessary to manually delete models in "fixed" to produce new models. To reproduce the evaluation, execute QLearning.java.

Executing GUI.java launches a graphical application in where users can introduce their preferences to fix a model. They can choose if they want to export the repaired model and where to do so. The algorithm gets experience as models are repaired, this experience or knowledge is stored in knowledege.properties. This file allows to share knowledge between different users, reusing what was learnt from users with the same preferences. At the moment, the GUI only allows to select one model at a time.

Be aware of the random nature of Reinforcement Learning algorithm, this can lead to different results in different executions.

If you want to reset the algorithm's knowledge, just delete all content inside knowledge.xml.

Contact: abar@hvl.no

## How to download the project
1. Clone the project and its submodules: </br>
  `git clone --recursive https://github.com/MagMar94/ParmorelEclipsePlugin.git` </br>
  If you are not familiar with submodules, you can have a look at [Vogella](https://www.vogella.com/tutorials/GitSubmodules/article.html) or at the [Git-documentation](https://git-scm.com/book/en/v2/Git-Tools-Submodules).
2. Make sure you are running Eclipse Version 2018-12 (4.10.0) or a compatible version, and have installed the Eclipse Modeling Tools. Eclipse-packages can be found [here](https://www.eclipse.org/downloads/packages/).
3. Make sure you have the Plug-in Development Environment.

## How to run the Main-class
If it has been run before, delete all the models from the fixed-folder. Run again.

## How to run the GUI-class
If it has been run before, delete the coresponding fixed model. Run again.
