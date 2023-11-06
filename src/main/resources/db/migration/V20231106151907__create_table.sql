CREATE TABLE IF NOT EXISTS `media` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `filename` VARCHAR(64) NOT NULL DEFAULT '',
    `type` TINYINT(3) NOT NULL,
    `source_dir_path` VARCHAR(255) NOT NULL DEFAULT '',
    `target_file_path` VARCHAR(255) NOT NULL DEFAULT '',
    `reverse_source_dir` VARCHAR(255) NOT NULL DEFAULT '',
    `result` TINYINT(3) NOT NULL ,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    key `index_source_filename` (reverse_source_dir(20), `filename`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE IF NOT EXISTS `failed_jobs` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `filename` VARCHAR(64) NOT NULL DEFAULT '',
    `type` TINYINT(3) NOT NULL,
    `source_dir_path` VARCHAR(255) NOT NULL DEFAULT '',
    `target_file_path` VARCHAR(255) NOT NULL DEFAULT '',
    `reverse_source_dir` VARCHAR(255) NOT NULL DEFAULT '',
    `reason` TINYINT(3) NOT NULL ,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    key `index_source_filename` (reverse_source_dir(20), `filename`)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci;