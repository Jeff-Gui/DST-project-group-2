CREATE TABLE vep(
	sample_id INT,
	Uploaded_variation VARCHAR,
	Location VARCHAR,
	Allele VARCHAR,
	Gene VARCHAR,
	Feature VARCHAR,
	Feature_type VARCHAR,
	Consequence VARCHAR,
	cDNA_position VARCHAR,
	CDS_position VARCHAR,
	Protein_position VARCHAR,
	Amino_acids VARCHAR,
	Codons VARCHAR,
	Existing_variation VARCHAR,
	Extra TEXT
);

INSERT INTO vep(sample_id, Uploaded_variation, Location, Allele, Gene, Feature, Feature_type, Consequence, cDNA_position, CDS_position, Protein_position, Amino_acids, Codons, Existing_variation, Extra)
VALUES (1,1,1,1,1,1,1,1,1,1,1,1,1,1,1);

SELECT * FROM vep 
WHERE extra LIKE 'IMPACT=MODIFIER%'
LIMIT 1000;

SELECT location, allele, feature, feature_type, consequence, extra FROM vep
WHERE gene = 'ENSG00000248527' AND sample_id = 0;

SELECT location, allele, feature, feature_type, consequence, extra FROM vep WHERE gene = '''ENSG00000248527''' AND sample_id = 0

-- clinic metadata.tsv
CREATE TABLE clinic_meta(
	ID INT PRIMARY KEY,
	location VARCHAR,
	gene VARCHAR,
	evidenceLevel VARCHAR,
	clinical_annotation_types VARCHAR,
	genotype_phenotype_ids VARCHAR,
	annotation_text TEXT,
	variant_annotation_ids TEXT,
	variant_annotations TEXT,
	PMIDs TEXT,
	evidence_count INT,
	related_chemicals VARCHAR,
	related_diseases TEXT,
	biogeographical_groups VARCHAR,
	chromosome VARCHAR
)

INSERT INTO clinic_meta(ID,location,gene,evidenceLevel,clinical_annotation_types,
						genotype_phenotype_ids,annotation_text,variant_annotation_ids,
					   variant_annotations,PMIDs,evidence_count,related_chemicals,
					   related_diseases,biogeographical_groups,chromosome)
					   VALUES (1,'2','3','4','5','6','7','8','9','10',11,'12','13','14',15);

SELECT * FROM clinic_meta;
DELETE FROM clinic_meta;

-- clinic annotation.tsv
CREATE TABLE clinic_ann(
	genotype_phenotype_id INT PRIMARY KEY,
	genotype VARCHAR,
	annotation TEXT
);

INSERT INTO clinic_ann(genotype_phenotype_id, genotype, annotation)
VALUES (1,'2','3');

SELECT * FROM clinic_ann;
DELETE FROM clinic_ann;

-- \\copy public.clinic_ann (genotype_phenotype_id, genotype, annotation) FROM '/Users/jefft/Desktop/DST-project-group-2/PGKB data/annotation_data/annotations/clinical_ann.tsv' DELIMITER E'\\t' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';''

SELECT * FROM clinic_ann WHERE genotype_phenotype_id IN (
	SELECT genotype_phenotype_ids FROM clinic_meta WHERE ID=981755803
);

SELECT * FROM clinic_ann WHERE genotype_phenotype_id=1450043467;

-- Variant.tsv
CREATE TABLE variant(
	variant_id VARCHAR,
	variant_name VARCHAR,
	gene_ids VARCHAR,
	gene_symbols VARCHAR,
	location VARCHAR,
	variant_annotation_count INT,
	clinical_annotation_count INT,
	high_level_clinical_annotation INT,
	guidance_annotation_count INT,
	label_annotation_count INT,
	synonyms TEXT
);

-- \\copy public.variant (variant_id, variant_name, gene_ids, gene_symbols, location, variant_annotation_count, clinical_annotation_count, high_level_clinical_annotation, guidance_annotation_count, label_annotation_count, synonyms) FROM '/Users/jefft/Desktop/DST-project-group-2/PGKB data/primary_data/variants/variants.tsv' DELIMITER E'\\t' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';"

SELECT * FROM variant LIMIT 100;

-- do Match
SELECT DISTINCT location, allele FROM vep WHERE sample_id=0;

SELECT variant_name FROM variant WHERE location='NC_000004.11:130165464' AND clinical_annotation_count!=0;

-- gene mapping (gene_treated.tsv)
CREATE TABLE gene(
	pgkb_id VARCHAR,
	ncbi_id VARCHAR,
	hgnc_id VARCHAR,
	es_id_old VARCHAR,
	name VARCHAR,
	symbol VARCHAR,
	alternative_names TEXT,
	alternative_symbols TEXT,
	isVIP VARCHAR,
	hasVariantAnnotation VARCHAR,
	cross_reference TEXT,
	hasCPICguidline VARCHAR,
	chromosome VARCHAR,
	ch37_start VARCHAR,
	ch37_stop VARCHAR,
	ch38_start VARCHAR,
	ch38_stop VARCHAR,
	ensembl_id VARCHAR
);

DROP TABLE gene;
DELETE FROM gene;

SELECT symbol FROM gene WHERE Ensembl_id='ENSG00000128274';

-- Drug
create table drug
(
    id varchar(100) not null,
    name varchar(500) null,
    obj_cls varchar(100) null,
    drug_url varchar(100) null,
    biomarker boolean null,
    constraint drug_id_uindex
        unique (id)
);

alter table drug
    add primary key (id);

-- Drug label
create table drug_label
(
    id varchar(100) not null,
    name varchar(200) null,
    obj_cls varchar(100) null,
    alternate_drug_available boolean null,
    dosing_information boolean null,
    prescribing_markdown varchar(2000) null,
    source varchar(100) null,
    text_markdown varchar(4000) null,
    summary_markdown varchar(1000) null,
    raw text null,
    drug_id varchar(100) null,
    constraint drug_label_id_uindex
        unique (id)
);

alter table drug_label
    add primary key (id);

-- Dosing guideline
create table dosing_guideline
(
    id varchar(250) not null,
    obj_cls varchar(100) null,
    name varchar(100) null,
    recommendation boolean null,
    drug_id varchar(250) null,
    source varchar(1000) null,
    summary_markdown varchar(2000) null,
    text_markdown text null,
    raw text null,
    constraint dosing_guideline_id_uindex
        unique (id)
);

alter table dosing_guideline
    add primary key (id);

select drug_label.id,drug_label.alternate_drug_available,
drug_label.source,drug_label.summary_markdown,
drug_label.drug_id,drug.name into drugNames 
from drug right outer join drug_label on drug.id=drug_label.drug_id;

select dosing_guideline.name,drug.name as drug ,dosing_guideline.source,
dosing_guideline.recommendation,dosing_guideline.summary_markdown 
into dosing_guideline_name 
from drug right outer join dosing_guideline 
on drug.id=dosing_guideline.drug_id;

-- var-drug-ann
create table var_drug_ann(
annotation_id varchar(250),
variant varchar(250),
gene varchar(250),
chemical varchar(2500),
pmid varchar(250),
phenotype_category varchar(250),
significance varchar(250),
notes varchar(2500),
sentence varchar(2500),
studyparam varchar(250),
allels varchar(250),
choromosome varchar(250))

COPY var_drug_ann(annotation_id ,
variant ,
gene ,
chemical ,
pmid ,
phenotype_category ,
significance ,
notes ,
sentence ,
studyparam ,
allels ,
choromosome ) from 'C:\var_drug_ann.tsv' DELIMITER '	' NULL AS '\N';

-- variants
create table variants(
	variantID VARCHAR(250) NOT NULL,
	variant VARCHAR(250),
	geneID VARCHAR(250),
	geneSymbol VARCHAR(500),
	"location" VARCHAR(500),
	varAnnCount INT,
	cliAnnCount INT,
	LevCliAnnCount INT,
	GdlAnnCount INT,
	LabAnnCount INT,
	synonyms VARCHAR(50000)
);
COPY variants(
variantID,variant,geneID,geneSymbol,"location",varAnnCount,
	cliAnnCount,
	LevCliAnnCount,
	GdlAnnCount,
	LabAnnCount,
	synonyms) from 'C:\variants.tsv' DELIMITER '	' NULL AS '\N' ;


SELECT var_drug_ann.variant, var_drug_ann.gene,
var_drug_ann.chemical,var_drug_ann.significance,
var_drug_ann.notes,var_drug_ann.sentence,
variants.location
INTO location_annvar FROM variants right outer join var_drug_ann 
on variants.variant=var_drug_ann.variant;
select * from location_annvar;

-- symbol
Create table symbol(
Gene_id int,
Symbol varchar(150),
)
Create table ensembl(
Gene_id int,
Ensemble_id varchar(150),
)

select symbol.gene_id,symbol.symbol,ensembl.ensemble_id
into symbol_ensembl
from ensembl right outer join symbol 
on symbol.gene_id=ensembl.gene_id
copy ensembl from 'C:/ensembl.csv' with csv header
copy symbol from 'C:/symbol.csv' with csv header

-- vcf
create table vcf(
	sampleID int,
	Uploaded_variation varchar(100),	
	"Location" varchar(50),	
		Allele varchar(50),	
		Gene varchar(50),
		Feature	varchar(50),
		Feature_type varchar(100),	
		Consequence	varchar(100),
		cDNA_position varchar(50),	
		CDS_position varchar(50),
		Protein_position varchar(50),	
		Amino_acids	varchar(50),
		Codons varchar(50),
		Existing_variation varchar(50),	
		Extra varchar(100))