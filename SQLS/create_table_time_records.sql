CREATE TABLE time_records (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NOT NULL,

                              time_in DATETIME NOT NULL,
                              time_out DATETIME NULL,

                              total_hours DOUBLE,
                              break_hours DOUBLE,
                              overtime_hours DOUBLE,

                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT fk_user
                                  FOREIGN KEY (user_id)
                                      REFERENCES users(id)
                                      ON DELETE CASCADE
);