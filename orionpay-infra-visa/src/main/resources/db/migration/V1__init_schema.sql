-- =========================================================================
-- Migração Flyway: V1__init_schema.sql
-- Descrição: Criação do Schema inicial para processamento de arquivos Visa
-- =========================================================================

-- Schema principal
CREATE SCHEMA IF NOT EXISTS visa_interchange;

-- 1. Base Audit e Arquivos (Controles Iniciais)
CREATE TABLE IF NOT EXISTS visa_interchange.audit_log_base (
	correlation_uuid varchar(36) NOT NULL,
	raw_line_content varchar(170) NULL,
	processing_status_id int4 NULL,
	status_message text NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NULL,
	CONSTRAINT audit_log_base_pkey PRIMARY KEY (correlation_uuid)
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_audit_base (
	"uuid" varchar(36) NOT NULL,
	raw_text_row varchar(170) NULL,
	processing_status_code int4 NULL,
	processing_description text NULL,
	date_created timestamp NOT NULL,
	date_updated timestamp NULL,
	CONSTRAINT visa_audit_base_pkey PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_file_control (
	id bigserial NOT NULL,
	audit_uuid varchar(36) NULL,
	file_name varchar(255) NULL,
	file_type_indicator varchar(10) NULL,
	source_bin varchar(6) NULL,
	transmission_date date NULL,
	total_record_count int4 NULL,
	total_amount numeric(19, 2) NULL,
	status_name varchar(20) NULL,
	control_movement_date date NULL,
	CONSTRAINT visa_file_control_pkey PRIMARY KEY (id),
	CONSTRAINT visa_file_control_audit_uuid_fkey FOREIGN KEY (audit_uuid) REFERENCES visa_interchange.audit_log_base(correlation_uuid)
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_file_header (
	id bigserial NOT NULL,
	audit_uuid varchar(36) NULL,
	file_type_code varchar(10) NULL,
	processing_bin varchar(6) NULL,
	transmission_date date NULL,
	unique_file_id varchar(30) NULL,
	is_test_environment bool NULL,
	release_number varchar(3) NULL,
	security_code varchar(8) NULL,
	CONSTRAINT visa_file_header_pkey PRIMARY KEY (id),
	CONSTRAINT visa_file_header_audit_uuid_fkey FOREIGN KEY (audit_uuid) REFERENCES visa_interchange.audit_log_base(correlation_uuid)
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_file_headers (
	id bigserial NOT NULL,
	audit_uuid varchar(36) NULL,
	file_type_label varchar(10) NULL,
	processing_bin varchar(6) NULL,
	transmission_date date NULL,
	unique_file_id varchar(30) NULL,
	test_option_flag varchar(4) NULL,
	security_code varchar(8) NULL,
	delivery_code varchar(1) NULL,
	release_number varchar(3) NULL,
	CONSTRAINT visa_file_headers_pkey PRIMARY KEY (id),
	CONSTRAINT visa_file_headers_audit_uuid_fkey FOREIGN KEY (audit_uuid) REFERENCES visa_interchange.visa_audit_base("uuid")
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_file_summary (
	id bigserial NOT NULL,
	header_id int8 NULL,
	batch_number varchar(6) NULL,
	total_transactions_count int4 NULL,
	monetary_records_count int4 NULL,
	total_tcr_rows_count int4 NULL,
	total_aggregate_amount numeric(19, 2) NULL,
	file_status_name varchar(20) NULL,
	movement_reference_date date NULL,
	CONSTRAINT visa_file_summary_pkey PRIMARY KEY (id),
	CONSTRAINT visa_file_summary_header_id_fkey FOREIGN KEY (header_id) REFERENCES visa_interchange.visa_file_header(id)
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_file_trailers (
	id bigserial NOT NULL,
	file_header_id int8 NULL,
	batch_number varchar(6) NULL,
	transaction_count int4 NULL,
	monetary_record_count int4 NULL,
	total_record_count int4 NULL,
	total_destination_amount numeric(19, 2) NULL,
	hash_total_hexa varchar(4) NULL,
	status_name varchar(20) NULL,
	source_file_name varchar(255) NULL,
	movement_date date NULL,
	CONSTRAINT visa_file_trailers_pkey PRIMARY KEY (id),
	CONSTRAINT visa_file_trailers_file_header_id_fkey FOREIGN KEY (file_header_id) REFERENCES visa_interchange.visa_file_headers(id)
);


-- 2. Entidades Secundárias e Regras (Configuração Inicial)
CREATE TABLE IF NOT EXISTS visa_interchange.bin_list (
	bin_code varchar(8) NOT NULL,
	brand varchar(20) DEFAULT 'VISA',
	product_name varchar(50) NULL,
	card_type varchar(20) NULL,
	is_national bool DEFAULT true,
	uuid varchar(36) NOT NULL,
	date_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	date_updated timestamp NULL,
	CONSTRAINT bin_list_pkey PRIMARY KEY (bin_code)
);

CREATE TABLE IF NOT EXISTS visa_interchange.interchange_rules (
	id serial4 NOT NULL,
	product_name varchar(50) NULL,
	transaction_channel varchar(20) NULL,
	interchange_rate numeric(5, 4) NULL,
	fixed_fee numeric(10, 2) DEFAULT 0.00,
	valid_from date NULL,
	valid_until date NULL,
	uuid varchar(36) NOT NULL,
	date_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	date_updated timestamp NULL,
	CONSTRAINT interchange_rules_pkey PRIMARY KEY (id),
	CONSTRAINT unique_rule UNIQUE (product_name, transaction_channel, valid_from)
);


-- 3. Transações de Liquidação Master (TC50 Principal)
CREATE TABLE IF NOT EXISTS visa_interchange.visa_settlement_master (
	id bigserial NOT NULL,
	file_control_id int8 NULL,
	transaction_id_orionpay varchar(50) NULL, -- Removido Not Null estrito até preencher via Domain
	card_bin varchar(8) NOT NULL,
	merchant_id_visa varchar(15) NULL,
	amount_gross numeric(19, 2) NOT NULL,
	amount_interchange numeric(19, 2) NULL,
	currency_code varchar(3) DEFAULT '986',
	date_transaction date NULL,
	settlement_date date NULL,
	auth_code varchar(6) NULL,
	rrn_number varchar(12) NULL,
	audit_uuid varchar(36) NOT NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP,
	usage_code varchar(2) NULL, -- Ampliado para suportar espaços eventuais
	reason_code varchar(4) NULL,
	settlement_flag varchar(1) NULL,
    reconciliation_status varchar(20) DEFAULT 'PENDING',
	CONSTRAINT visa_settlement_master_pkey PRIMARY KEY (id),
    CONSTRAINT visa_settlement_master_audit_fkey FOREIGN KEY (audit_uuid) REFERENCES visa_interchange.audit_log_base(correlation_uuid)
);
CREATE INDEX IF NOT EXISTS idx_visa_master_bin ON visa_interchange.visa_settlement_master USING btree (card_bin);
CREATE INDEX IF NOT EXISTS idx_visa_master_date ON visa_interchange.visa_settlement_master USING btree (settlement_date);
CREATE INDEX IF NOT EXISTS idx_visa_settlement_rrn ON visa_interchange.visa_settlement_master USING btree (rrn_number);


-- 4. Conciliação e Histórico (Reconciliation)
CREATE TABLE IF NOT EXISTS visa_interchange.visa_reconciliation_history (
    id bigserial NOT NULL,
    settlement_master_id int8 NOT NULL,
    old_status varchar(20) NULL,
    new_status varchar(20) NOT NULL,
    reason varchar(255) NULL,
    changed_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT visa_reconciliation_history_pkey PRIMARY KEY (id),
    CONSTRAINT visa_reconciliation_history_master_fkey FOREIGN KEY (settlement_master_id) REFERENCES visa_interchange.visa_settlement_master(id)
);


-- 5. Entidades Auxiliares TC50 (Detalhes, Apresentação e Disputas)
CREATE TABLE IF NOT EXISTS visa_interchange.visa_settlement_details (
	id bigserial NOT NULL,
	master_settlement_id int8 NULL,
	merchant_dba_name varchar(75) NULL,
	merchant_legal_name varchar(75) NULL,
	merchant_address_line1 varchar(60) NULL,
	merchant_address_line2 varchar(60) NULL,
	merchant_city varchar(29) NULL,
	mcc_primary varchar(4) NULL,
	mcc_secondary varchar(4) NULL,
	acquiring_bin_1 varchar(6) NULL,
	card_acceptor_id_1 varchar(15) NULL,
	acquiring_bin_2 varchar(6) NULL,
	card_acceptor_id_2 varchar(15) NULL,
	CONSTRAINT visa_settlement_details_pkey PRIMARY KEY (id),
    CONSTRAINT visa_settlement_details_master_fkey FOREIGN KEY (master_settlement_id) REFERENCES visa_interchange.visa_settlement_master(id)
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_clearing_presentation (
	id bigserial NOT NULL,
	country_code varchar(3) NULL,
	settlement_type varchar(3) NULL,
	national_reimbursement_fee numeric(19, 2) NULL,
	installment_count int4 NULL,
	transaction_type varchar(2) NULL,
	card_sequence_number varchar(3) NULL,
	terminal_verification_results varchar(10) NULL,
	cryptogram_amount numeric(19, 2) NULL,
	issuer_application_data text NULL,
	CONSTRAINT visa_clearing_presentation_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_dispute_management (
	id bigserial NOT NULL,
	return_reason_code_primary varchar(3) NULL,
	return_reason_code_secondary varchar(3) NULL,
	original_source_amount numeric(19, 2) NULL,
	original_source_currency varchar(3) NULL,
	fee_program_indicator_submitted varchar(3) NULL,
	fee_program_indicator_assessed varchar(3) NULL,
	interchange_fee_amount numeric(19, 2) NULL,
	interchange_fee_sign varchar(1) NULL,
	CONSTRAINT visa_dispute_management_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS visa_interchange.visa_national_reversal_details (
	id bigserial NOT NULL,
	clearing_record_id int8 NULL,
	national_reimbursement_fee numeric(19, 2) NULL,
	settlement_type varchar(3) NULL,
	reason_code varchar(4) NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT visa_national_reversal_details_pkey PRIMARY KEY (id)
);

-- 6. Outras Tabelas do Modelo Clássico da Visa
CREATE TABLE IF NOT EXISTS visa_interchange.raw_tc50_settlement (
	id bigserial NOT NULL,
	file_name varchar(100) NULL,
	tcr_code varchar(2) NULL,
	transaction_id_original varchar(50) NULL,
	bin varchar(8) NULL,
	merchant_id varchar(15) NULL,
	transaction_amount numeric(19, 2) NULL,
	interchange_amount numeric(19, 2) NULL,
	reimbursement_attribute varchar(1) NULL,
	settlement_date date NULL,
	processing_status varchar(20) NULL,
	error_code varchar(10) NULL,
	uuid varchar(36) NOT NULL,
	date_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	date_updated timestamp NULL,
	CONSTRAINT raw_tc50_settlement_pkey PRIMARY KEY (id),
	CONSTRAINT fk_bin_info FOREIGN KEY (bin) REFERENCES visa_interchange.bin_list(bin_code)
);

CREATE TABLE IF NOT EXISTS visa_interchange.sensitive_data_log (
	id bigserial NOT NULL,
	tc50_id int8 NULL,
	encrypted_pan bytea NOT NULL,
	uuid varchar(36) NOT NULL,
	date_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	date_updated timestamp NULL,
	CONSTRAINT sensitive_data_log_pkey PRIMARY KEY (id),
	CONSTRAINT sensitive_data_log_tc50_id_fkey FOREIGN KEY (tc50_id) REFERENCES visa_interchange.raw_tc50_settlement(id)
);
