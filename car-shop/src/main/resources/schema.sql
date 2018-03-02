 CREATE TABLE cars
    (id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    registration VARCHAR(10)UNIQUE NOT NULL,
	  brand VARCHAR(20) NOT NULL,
	  color VARCHAR(20) NOT NULL,
	  car_year INT NOT NULL
	 );
	 
CREATE TABLE person
     (id BIGINT AUTO_INCREMENT PRIMARY KEY ,
      contact VARCHAR(100)UNIQUE NOT NULL
	 );

CREATE TABLE cars_shop
	(id BIGINT AUTO_INCREMENT PRIMARY KEY,
	person_id BIGINT NOT NULL ,
	car_id BIGINT UNIQUE NOT NULL ,
	price INT NOT NULL,
	FOREIGN KEY (person_id) REFERENCES person (id),
	FOREIGN KEY (car_id) REFERENCES cars (id)
	);
	
CREATE TABLE deals
	(id BIGINT AUTO_INCREMENT PRIMARY KEY,
	car_for_sale_id BIGINT NOT NULL ,
	person_id BIGINT NOT NULL,
	buyer_price INT NOT NULL,
	deal_status VARCHAR (200)NOT NULL,
	FOREIGN KEY (person_id) REFERENCES person (id),
	FOREIGN KEY (car_for_sale_id) REFERENCES cars_shop (id)
	);

 