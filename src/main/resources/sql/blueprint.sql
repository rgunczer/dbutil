CREATE TABLE blue_sample(
    sample_id int IDENTITY(1,1) NOT NULL,
    sample_key varchar(64) NOT NULL,
    sample_value nvarchar(4000) NULL
)