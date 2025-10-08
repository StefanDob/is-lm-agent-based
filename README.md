# Agent-Based Simulation (Repast Project)

## Overview

This project is an agent-based macroeconomic simulation built with Repast Simphony, modeling the interactions between households and firms. The simulation tracks key economic indicators such as employment, wages, prices, inventory, and inequality over time, allowing for both micro- and macroeconomic analysis.

The model incorporates:
Households (Haushalt) making consumption decisions and seeking employment.
Firms (Unternehmen) producing goods, adjusting wages and prices, and managing employment.
Export and data analysis modules (ExportManager, CSVExporter, ExcelExporter, DataPlotter) to record daily, monthly, and semiannual economic data.
Inequality measurement using Gini coefficients.
Unemployment tracking over time.

## Model Versions
Each branch in this repository represents a different version of the simulation model, reflecting various stages of development and refinement:

### InitialModel

Replicates Lengnick’s original model as closely as possible.
This version highlights a key issue in the original setup — a persistently high unemployment rate.

### Main

A near-faithful replication of Lengnick’s model with only minimal adjustments.
Unlike the original, this version prevents agents from artificially increasing wages and later paying only a reduced “crisis wage” under financial stress.

### FixedModel

An improved version of the Lengnick model that implements several fixes to achieve a more stable and functional economy.

### NewModel

Extends the model by introducing a government (state) actor.


## Setup

### Setup Repast
Setup Repast, see Installation guide at: https://repast.github.io/download.html
Please note the project has been tested in Version 2.11.0 and the correspoding Java 17.0.10 (openjdk).

### Clone the Repository
If you want to use Github the following specification might help. In order to clone the project use:
'''
git clone https://github.com/yourusername/islm-repast.git
cd islm-repast
'''

To switch between branch (i.e. in order to switch to branch newModel) use:
'''
git checkout newModel 
'''
Note that each branch corresponds a model.

Alternatively the code can be downloaded directly from Google Drive - each folder in the root directory corresponds to a model version. No git is needed however a new import is needed for each version of the project.

### Import Project
Launch Repast Simphony.
Select Import → Existing Project into Workspace.
Navigate to the project folder and finish the import.
Start the simulation as Modell (not server).

## Simulation Run
Once you run the project you will see a GUI pop up. Click Run Simulation in that GUI (green triangle).
The simulation will execute for 147,000 ticks (~7000 months).
Scheduled methods handle daily, monthly, and semiannual routines automatically.
You can see trackers of the economy in the right side of the GUI. Note that those charts are only there for an overview and should not be used for exesessive economic analysis.

### Access Ouputs

All outputs are saved in the output/ folder:

islm_daily_output.csv	Daily metrics such as unmet demand ratio
islm_monthly_output.csv	Monthly metrics including wages, prices, inventory, and employment
islm_semianual_output.csv	Semiannual metrics and price deltas
unmet_demand_density.png	Histogram of unmet demand ratio
islm_output.xlsx	Consolidated Excel report of daily and monthly data

The primary output file is islm_output.xlsx, as it contains the charts used in the thesis. It consists of four sheets:
Daily Data and Monthly Data: Track the corresponding metrics and are overwritten with each simulation run.
PDF Histogram: Optional; not required for thesis analysis.
Charts: Contains the visualizations referenced in the thesis. These charts update automatically based on the data in the other sheets, ensuring that they always reflect the latest simulation results.

Additional analyses, including the Phillips curve, can be found in the accompanying Python Jupyter notebook.


## Main
The Main Model branch represents the version of the project that aims to replicate Lengnick’s original model with only minimal modifications. In contrast to Lengnick’s model, this version prevents agents from simply increasing wages and later paying only a reduced “crisis wage” when financial pressure arises.
