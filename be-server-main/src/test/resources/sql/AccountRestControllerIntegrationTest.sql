
INSERT INTO account (id, email, password, nickname, profile_url, created_date, modified_date, created_by, updated_by)
VALUES
--     (UUID(), 'example1@gmail.com', 'password_hash_1', 'nickname1', 'profileUrl1', NOW(), NOW(), 'admin', 'admin'),
--     (UUID(), 'example2@gmail.com', 'password_hash_2', 'nickname2', 'profileUrl2', NOW(), NOW(), 'admin', 'admin'),
--     (UUID(), 'example3@gmail.com', 'password_hash_3', 'nickname3', 'profileUrl3', NOW(), NOW(), 'admin', 'admin'),
    (UUID(), 'example1@gmail.com', '$2a$10$mvmYVAgMluwMJGu0si5JNO70jhJH8r/VKemBwYEgXFDPQYOB80dCO', 'nickname1', 'profileUrl1', NOW(), NOW(), 'admin', 'admin'),
    (UUID(), 'example2@gmail.com', '$2a$10$iDzMv433GWTJ3nLuRyzVdeetaH09gPAvxcqg8zpdqVz9rcJjOrIuu', 'nickname2', 'profileUrl2', NOW(), NOW(), 'admin', 'admin'),
    (UUID(), 'example3@gmail.com', '$2a$10$7Qj2u05LdyC87kHMAZHLO.w3LkV2tsN9SKcTjt1zzoX9KreTwGuYy', 'nickname3', 'profileUrl3', NOW(), NOW(), 'admin', 'admin');