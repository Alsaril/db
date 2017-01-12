CREATE TABLE IF NOT EXISTS user (
  id          INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(255) NULL,
  about       TEXT         NULL,
  name        VARCHAR(255) NULL,
  email       VARCHAR(255) NOT NULL UNIQUE,
  isAnonymous BOOLEAN      NOT NULL
)
  CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS forum (
  id        INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  shortname VARCHAR(255) NOT NULL,
  user_id   INT          NOT NULL,
  FOREIGN KEY (user_id) REFERENCES user (id),
  UNIQUE INDEX un_shortname(shortname)
)
  CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS thread (
  id        INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  isDeleted BOOLEAN      NOT NULL,
  forum_id  INT          NOT NULL,
  title     VARCHAR(255) NOT NULL,
  isClosed  BOOLEAN      NOT NULL,
  user_id   INT          NOT NULL,
  date      DATETIME     NOT NULL,
  message   TEXT         NOT NULL,
  slug      TEXT         NOT NULL,
  likes     INT          NOT NULL DEFAULT 0,
  dislikes  INT          NOT NULL DEFAULT 0,
  points    INT          NOT NULL DEFAULT 0,
  posts     INT          NOT NULL DEFAULT 0,
  FOREIGN KEY (forum_id) REFERENCES forum (id),
  FOREIGN KEY (user_id) REFERENCES user (id)
)
  CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS post (
  id            INT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parent_id     INT      NULL,
  isApproved    BOOLEAN  NOT NULL,
  isHighlighted BOOLEAN  NOT NULL,
  isEdited      BOOLEAN  NOT NULL,
  isSpam        BOOLEAN  NOT NULL,
  isDeleted     BOOLEAN  NOT NULL,
  date          DATETIME NOT NULL,
  thread_id     INT      NOT NULL,
  message       TEXT     NOT NULL,
  user_id       INT      NOT NULL,
  forum_id      INT      NOT NULL,
  likes         INT      NOT NULL DEFAULT 0,
  dislikes      INT      NOT NULL DEFAULT 0,
  points        INT      NOT NULL DEFAULT 0,
  FOREIGN KEY (parent_id) REFERENCES post (id),
  FOREIGN KEY (thread_id) REFERENCES thread (id),
  FOREIGN KEY (user_id) REFERENCES user (id),
  FOREIGN KEY (forum_id) REFERENCES forum (id)
)
  CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS follow (
  id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  followee_id INT NOT NULL,
  follower_id INT NOT NULL,
  FOREIGN KEY (followee_id) REFERENCES user (id),
  FOREIGN KEY (follower_id) REFERENCES user (id),
  UNIQUE INDEX follow_unique (followee_id, follower_id)
);

CREATE TABLE IF NOT EXISTS thread_vote (
  id        INT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
  thread_id INT     NOT NULL,
  user_id   INT     NOT NULL,
  value     BOOLEAN NOT NULL,
  FOREIGN KEY (thread_id) REFERENCES thread (id),
  FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS post_vote (
  id      INT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
  post_id INT     NOT NULL,
  user_id INT     NOT NULL,
  value   BOOLEAN NOT NULL,
  FOREIGN KEY (post_id) REFERENCES post (id),
  FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS subscription (
  id        INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  thread_id INT NOT NULL,
  user_id   INT NOT NULL,
  FOREIGN KEY (thread_id) REFERENCES thread (id),
  FOREIGN KEY (user_id) REFERENCES user (id),
  UNIQUE INDEX subscription_unique (thread_id, user_id)
);