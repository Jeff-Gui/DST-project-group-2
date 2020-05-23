-- vep data (uploaded by user)
CREATE TABLE vep(
	sample_id INT,
	Uploaded_variation VARCHAR,
	location VARCHAR,
	allele VARCHAR,
	gene VARCHAR,
	feature VARCHAR,
	feature_type VARCHAR,
	consequence VARCHAR,
	cdna_position VARCHAR,
	cds_position VARCHAR,
	protein_position VARCHAR,
	amino_acids VARCHAR,
	codons VARCHAR,
	existing_variation VARCHAR,
	extra TEXT
);

-- annovar data (uploaded by user)
CREATE TABLE annovar(
	sample_id INT,
	chr VARCHAR,
	"Start" VARCHAR,
	"End" VARCHAR,
	"Ref" VARCHAR,
	alt VARCHAR,
	"Func.refGene" TEXT,
	"Gene.refGene" TEXT,
	"GeneDetail.refGene" TEXT,
	"ExonicFunc.refGene" TEXT,
	"AAChange.refGene" TEXT,
	cytoband TEXT
);

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
	evidence_count VARCHAR,
	related_chemicals VARCHAR,
	related_diseases TEXT,
	biogeographical_groups VARCHAR,
	chromosome VARCHAR
);

-- variant.tsv
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

-- gene_treated.tsv
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

-- drug.csv
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
ALTER TABLE drug
    ADD PRIMARY KEY (id);

-- drug label
CREATE TABLE drug_label
(
    id varchar(100) not null,
    name varchar(200) null,
    obj_cls varchar(100) null,
    alternate_drug_available boolean null,
    dosing_information boolean null,
    prescribing_markdown varchar(2000) null,
    source varchar(100) null,
    text_markdown varchar null,
    summary_markdown varchar null,
    raw text null,
    drug_id varchar(100) null,
    constraint drug_label_id_uindex
        unique (id)
);
ALTER TABLE drug_label
    ADD PRIMARY KEY (id);

-- dosing guideline
CREATE TABLE dosing_guideline
(
    id varchar not null,
    obj_cls varchar null,
    name varchar null,
    recommendation boolean null,
    drug_id varchar null,
    source varchar null,
    summary_markdown varchar null,
    text_markdown text null,
    raw text null,
    constraint dosing_guideline_id_uindex
        unique (id)
);
ALTER TABLE dosing_guideline
    ADD PRIMARY KEY (id);

-- var-drug-ann.tsv
CREATE TABLE var_drug_ann(
    annotation_id varchar(250),
    variant varchar(250),
    gene varchar(250),
    chemical varchar(2500),
    pmid varchar(250),
    phenotype_category varchar(250),
    significance varchar(250),
    notes varchar, -- not long enough?,
    sentence varchar, -- not long enough?
    studyparam varchar(250),
    allels varchar(250),
    choromosome varchar(250)
    );

-- users
CREATE TABLE users(
    username VARCHAR UNIQUE,
    passwords VARCHAR,
    email VARCHAR PRIMARY KEY
);
INSERT INTO users(username, passwords, email) VALUES ('zju','zju','*@*');

-- sample
CREATE TABLE sample(
    id INT PRIMARY KEY,
    created_at DATE,
    uploaded_by VARCHAR,
    sample_type VARCHAR
);

-- location_annvar: created from variant.tsv & var_drug_ann.tsv
CREATE OR REPLACE FUNCTION create_location_annvar()
  RETURNS VOID AS
  $$
	BEGIN
	EXECUTE format('CREATE TABLE location_annvar AS SELECT var_drug_ann.variant, var_drug_ann.gene,var_drug_ann.chemical,var_drug_ann.significance,var_drug_ann.notes,var_drug_ann.sentence,variant.location
FROM variant RIGHT OUTER JOIN var_drug_ann
ON variant.variant_name = var_drug_ann.variant;');
	END $$
LANGUAGE plpgsql;

-- drugNames from drug & drug label
CREATE OR REPLACE FUNCTION create_drugNames()
  RETURNS VOID AS
  $$
	BEGIN
	EXECUTE format('CREATE TABLE drugNames AS SELECT drug_label.id,drug_label.alternate_drug_available,drug_label.source,drug_label.summary_markdown, drug_label.drug_id,drug.name FROM drug RIGHT OUTER JOIN drug_label ON drug.id=drug_label.drug_id;');
	END $$
LANGUAGE plpgsql;


-- -- dosing_guideline_name from drug & dosing_guideline
CREATE OR REPLACE FUNCTION create_dosing_guideline_name()
  RETURNS VOID AS
  $$
	BEGIN
	EXECUTE format('CREATE TABLE dosing_guideline_name AS SELECT dosing_guideline.name,drug.name AS drug ,dosing_guideline.source,dosing_guideline.recommendation,dosing_guideline.summary_markdown FROM drug RIGHT OUTER JOIN dosing_guideline ON drug.id=dosing_guideline.drug_id;');
	END $$
LANGUAGE plpgsql;
