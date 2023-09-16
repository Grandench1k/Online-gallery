
<div align="center">
<h1><strong>online-gallery API v1</strong></h1>
  <a href="https://github.com/Grandench1k/online-gallery/issues/new?assignees=&labels=bug&template=01_BUG_REPORT.md&title=bug%3A+"><strong>Report a Bug</strong></a>
  ·
  <a href="https://github.com/Grandench1k/online-gallery/issues/new?assignees=&labels=enhancement&template=02_FEATURE_REQUEST.md&title=feat%3A+"><strong>Request a Feature</strong></a>
  .
  <a href="https://github.com/Grandench1k/online-gallery/issues/new?assignees=&labels=question&template=04_SUPPORT_QUESTION.md&title=support%3A+"><strong>Ask a Question</strong></a>
</div>

<div align="center">
<br />

[![Project license](https://img.shields.io/github/license/Grandench1k/online-gallery.svg?style=flat-square)](LICENSE)

[![Pull Requests welcome](https://img.shields.io/badge/PRs-welcome-ff69b4.svg?style=flat-square)](https://github.com/Grandench1k/online-gallery/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22)
[![code with love by Grandench1k](https://img.shields.io/badge/%3C%2F%3E%20with%20%E2%99%A5%20by-Grandench1k-ff1414.svg?style=flat-square)](https://github.com/Grandench1k)

</div>

<details open="open">
<summary><strong>Table of Contents</strong></summary>

- [About](#about)
  - [Built With](#built-with)
- [Getting Started](#getting-started)
  - [Installation and usage](#Installation_and_usage)
- [Roadmap](#roadmap)
- [Support](#support)
- [Project assistance](#project-assistance)
- [Contributing](#contributing)
- [Author](#author)
- [License](#license)

</details>

---

## About
 I present to you my online gallery API - a project in which I have invested all my passion and experience. This API is designed to provide users with access to their images and videos.
> It can be used as a backend for your frontend project.

### Built With
- [Java 17](https://www.oracle.com/java/technologies/downloads/#jdk17-windows) 
- [Maven](https://maven.apache.org/)
- [Spring framework](https://spring.io/)
- [MongoDB](https://www.mongodb.com/try/download/community)
- [Redis](https://redis.io/download/) (cache)
- [Docker](https://www.docker.com/products/docker-desktop/)
- [AWS S3](https://aws.amazon.com/ru/s3/)
- [OpenAPI](https://swagger.io/specification/)

## Getting Started
To use online-gallery API, you'll need to have Java SE 17 installed on your computer. You'll also need to clone this repository and set up a MongoDB and Redis databases. Then fill the application.properties based on your database

### Installation and usage



1. [Download and unpackage this project](https://github.com/Grandench1k/online-gallery/archive/refs/heads/main.zip).

or clone this project with git (On Windows, install [GIT Bash for Windows](https://gitforwindows.org/), on Linux install [GIT Bash for Linux](https://git-scm.com/download/linux) to run the command above)

```git clone https://github.com/Grandench1k/online-gallery.git```

2. Change application.properties file
 
application.properties can be found in the following directory
``src/main/resources/application.properties``

**application.properties**
```application.properties
##example - mongodb://{your host}:27017/online-gallery
spring.data.mongodb.uri=

##generated sha256 hash of a string is secret key.
##Follow this link you can generate it online - https://sha256.online/
jwt.secret=
##the time in millis. multiplied by 60.
jwt.access.token.expiration.date=
##the time in millis. multiplied by 60. Usually this number should be 2 times larger than the access jwt token.
jwt.refresh.token.expiration.date=

spring.servlet.multipart.enabled=true
spring.servlet.multipart.file-size-threshold=2KB
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=215MB
##region from aws
aws.region=
##aws s3 bucket name
aws.s3.buckets.main-bucket=

##mail host
spring.mail.host=
##mail port
spring.mail.port=
##your email, example - mail@example.org
spring.mail.username=
##this email password
spring.mail.password=
spring.mail.properties.mail.transport.protocol=smtp
##dubplicate mail port
spring.mail.properties.mail.smtp.port=
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

spring.cache.type=redis
##redis host
spring.data.redis.host=
##redis port
spring.data.redis.port=

springdoc.swagger-ui.path=swagger-ui.html
springdoc.api-docs.path=/online-gallery-docs
```
to get started, first enter all the empty values

3. After you have done all the above and launched the application through the IDE, then go to the swagger user interface page
```
example:
http://localhost:8080/swagger-ui/index.html
```
## Roadmap

See the [open issues](https://github.com/Grandench1k/online-gallery/issues) for a list of proposed features (and known issues).

- [Top Feature Requests](https://github.com/Grandench1k/online-gallery/issues?q=label%3Aenhancement+is%3Aopen+sort%3Areactions-%2B1-desc) (Add your votes using the 👍 reaction)
- [Top Bugs](https://github.com/Grandench1k/online-gallery/issues?q=is%3Aissue+is%3Aopen+label%3Abug+sort%3Areactions-%2B1-desc) (Add your votes using the 👍 reaction)
- [Newest Bugs](https://github.com/Grandench1k/online-gallery/issues?q=is%3Aopen+is%3Aissue+label%3Abug)

## Support
Reach out to the maintainer at one of the following places:

- [GitHub issues](https://github.com/Grandench1k/online-gallery/issues/new?assignees=&labels=question&template=04_SUPPORT_QUESTION.md&title=support%3A+)
- Contact options listed on [this GitHub profile](https://github.com/Grandench1k)

## Project assistance

If you want to say **thank you** or/and support active development of online-gallery API:

- Add a [GitHub Star](https://github.com/Grandench1k/online-gallery/) to the project.
- share this project with your developer friends.

Together, we can make online-gallery API **better**!

## Contributing

First off, thanks for taking the time to contribute! Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make will benefit everybody else and are **greatly appreciated**.

## Author

The original setup of this repository is by [Grandench1k](https://github.com/Grandench1k).

## License

This project is licensed under the **MIT license**.

See [LICENSE](LICENSE) for more information.
