# ROBOT

[ROBOT](https://bmcbioinformatics.biomedcentral.com/articles/10.1186/s12859-019-3002-3){:target="_blank"}. is Tool for Automating Ontology Workflows. The hpo2robot application is a GUI that streamlines the generation of input files for ROBOT.


## To discuss

- what should the final format of the table be?
- at what point of the pipeline should we be generating new term IDs and how can we tell Protege which IDs we have consumed?



Note that not all of these items will be shown in the table. The HPO id of the parent term will be shown
as a mouse-over hover, and the ORCID id is constant. The comment, synony, crossreferences will not be shown in the table,
and only the first 30 characters of the definition will be shown, followed by ellipsis.
To see all the data for any given row, the user will need to mark the row and then the entire
set of new data will be shown in the bottom left part of the GUI in a WebView.

|  HPO_ID | Term  | Synonyms  |Parents|Definition| PMIDs  | ORCID  | Crossreference  | Comments  |  Issue |
|:---|---|---|---|---|---|---|---|---|---|
| HP:0000613  | Photophobia  | Light hypersensitivity\|Extreme sensitivity of the eyes to light|Photodysphoria  |  Excessive sensitivity to light with the sensation of discomfort or pain in the eyes due to exposure to bright light |   | orcid:0000-0002-7356-1779  |   |   |     |
|?|	Mediastinal shift|	Shift of the mediastinum|	Abnormal mediastinum morphology|	A deviation of the mediastinum from its normal position in the midline of the thoracic cavity. The mediastinum is a compartment of the thoracic cavity that contains the heart and its blood vessels, the esophagus, trachea, thymus, as well as nerves and lymph nodes.|	PMID:35990927\|PMID:37680422	|orcid:0000-0002-7356-1779|		|Since the pleural cavity is confined by the rib cage, large pleural effusion may push the structures in the thoracic cavity to the opposite side resulting in a shift of the upper and lower mediastinum. Other etiologies are also observed. See Figures 1 and 2 of PMID:35990927 or Figure 1 of PMID:37680422.	|#10189|
|?|Bendopnea|| Dyspnea|Dyspnea as a result of bending forward while not holding one's breath; dyspnea or shortness of breath onset within 30 seconds of bending forward.|	PMID:31543202|	orcid:0000-0002-7356-1779| |		Bendopnea is a symptom mediated by increased ventricular filling pressure during bending forward. 	|#10180|