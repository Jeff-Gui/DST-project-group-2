-- vep data (uploaded by user)
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
    text_markdown varchar(4000) null,
    summary_markdown varchar(1000) null,
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
    notes varchar,
    sentence varchar,
    studyparam varchar(250),
    allels varchar(250),
    choromosome varchar(250)
    );

-- table import
copy public.clinic_meta (id, location, gene, evidencelevel, clinical_annotation_types, genotype_phenotype_ids, annotation_text, variant_annotation_ids, variant_annotations, pmids, evidence_count, related_chemicals, related_diseases, biogeographical_groups, chromosome) FROM './WebProject1/src/main/resources/Tables/clinical_ann_metadata.tsv' DELIMITER E'\\t' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';
copy public.variant (variant_id, variant_name, gene_ids, gene_symbols, location, variant_annotation_count, clinical_annotation_count, high_level_clinical_annotation, guidance_annotation_count, label_annotation_count, synonyms) FROM './WebProject1/src/main/resources/Tables/variant.tsv' DELIMITER E'\\t' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';
copy public.gene (pgkb_id, ncbi_id, hgnc_id, es_id_old, name, symbol, alternative_names, alternative_symbols, isvip, hasvariantannotation, cross_reference, hascpicguidline, chromosome, ch37_start, ch37_stop, ch38_start, ch38_stop, ensembl_id) FROM './WebProject1/src/main/resources/Tables/genes_treated.tsv' DELIMITER E'\\t' ENCODING 'UTF8';
copy public.variant (variant_id, variant_name, gene_ids, gene_symbols, location, variant_annotation_count, clinical_annotation_count, high_level_clinical_annotation, guidance_annotation_count, label_annotation_count, synonyms) FROM './WebProject1/src/main/resources/Tables/variant.tsv' DELIMITER E'\\t' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';
copy public.drug (id, name, obj_cls, drug_url, biomarker) FROM './WebProject1/src/main/resources/Tables/drug.csv' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';
copy public.drug_label (id, name, obj_cls, alternate_drug_available, dosing_information, prescribing_markdown, source, text_markdown, summary_markdown, \"raw\", drug_id) FROM './WebProject1/src/main/resources/Tables/drug_label.csv' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';
copy public.var_drug_ann (annotation_id, variant, gene, chemical, pmid, phenotype_category, significance, notes, sentence, studyparam, allels, choromosome) FROM './WebProject1/src/main/resources/Tables/var_drug_ann.tsv' DELIMITER E'\\t' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';
copy public.dosing_guideline (id, obj_cls, name, recommendation, drug_id, source, summary_markdown, text_markdown, \"raw\") FROM '/Users/jefft/Desktop/WebProject1/src/main/resources/Tables/dosing_guideline.csv' CSV HEADER ENCODING 'UTF8' QUOTE '\"' ESCAPE '''';

-- location_annvar: created from variant.tsv & variant.tsv
SELECT var_drug_ann.variant, var_drug_ann.gene,
var_drug_ann.chemical,var_drug_ann.significance,
var_drug_ann.notes,var_drug_ann.sentence,
variant.location
INTO location_annvar FROM variant RIGHT OUTER JOIN var_drug_ann
ON variant.variant_name = var_drug_ann.variant;
SELECT * FROM location_annvar;

-- drugNames from drug & drug label
SELECT drug_label.id,drug_label.alternate_drug_available,
drug_label.source,drug_label.summary_markdown,
drug_label.drug_id,drug.name INTO drugNames
FROM drug RIGHT OUTER JOIN drug_label ON drug.id=drug_label.drug_id;

-- dosing_guideline_name from drug & dosing_guideline
SELECT dosing_guideline.name,drug.name AS drug ,dosing_guideline.source,
dosing_guideline.recommendation,dosing_guideline.summary_markdown
INTO dosing_guideline_name
FROM drug RIGHT OUTER JOIN dosing_guideline
ON drug.id=dosing_guideline.drug_id;
