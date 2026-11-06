# MIM SIN FASHION Backend

Java 17, Spring Boot 3.5, Maven, Spring Data JPA, Hibernate এবং PostgreSQL দিয়ে modular monolith হিসেবে তৈরি হচ্ছে।

## Module boundaries

- `access` — authentication, users, roles ও permissions
- `staff` — employees, attendance ও payroll
- `catalog` — products ও categories
- `inventory` — stock, adjustments ও stock movement
- `procurement` — suppliers, purchase orders ও receiving
- `sales` — sales records; POS checkout-ও এই module-এর সঙ্গে যুক্ত হবে
- `returns` — sales return ও refund
- `finance` — expenses ও financial records
- `reports` — reports
- `dashboard` — summary data
- `settings` — shop configuration

প্রতিটি module-এর ভেতর `api`, `application`, `domain`, `infrastructure` layer রাখা হয়েছে। এগুলো এখন package skeleton; business logic পরের ধাপে যোগ হবে।

## Local database

PostgreSQL-এ `mimsin_fashion` database তৈরি করে environment variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` দিলে backend সংযোগ করবে। Development-এ Hibernate `ddl-auto=update` ব্যবহার করা হয়েছে। Flyway/Liquibase নেই।

Maven ইনস্টল হলে `mvn spring-boot:run` দিয়ে চালানো যাবে।
