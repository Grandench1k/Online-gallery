<div align="center">
<h1><strong>Online Gallery API v1</strong></h1>
  <p>An innovative solution for managing and showcasing your digital media.</p>
  <a href="https://github.com/Grandench1k/online-gallery/issues/new?assignees=&labels=bug&template=01_BUG_REPORT.md&title=bug%3A+"><strong>Report a Bug</strong></a>
  ·
  <a href="https://github.com/Grandench1k/online-gallery/issues/new?assignees=&labels=enhancement&template=02_FEATURE_REQUEST.md&title=feat%3A+"><strong>Request a Feature</strong></a>
  ·
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

<ul>
        <li><a href="#about">About</a>
            <ul>
                <li><a href="#built-with">Built With</a></li>
            </ul>
        </li>
        <li><a href="#getting-started">Getting Started</a>
            <ul>
                <li><a href="#installation-and-usage">Installation and Usage</a></li>
            </ul>
        </li>
        <li><a href="#roadmap">Roadmap</a></li>
        <li><a href="#support">Support</a></li>
        <li><a href="#project-assistance">Project Assistance</a></li>
        <li><a href="#contributing">Contributing</a></li>
        <li><a href="#author">Author</a></li>
        <li><a href="#license">License</a></li>
    </ul>
</details>

---

## About
 I present to you my online gallery API - a project in which I have invested all my passion and experience. This API is designed to provide users with access to their images and videos.
> It can be used as a backend for your frontend project.

## Built With
- [Java 17](https://www.oracle.com/java/technologies/downloads/#jdk17) 
- [Maven](https://maven.apache.org/)
- [Spring framework](https://spring.io/)
- [MongoDB](https://www.mongodb.com/)
- [Redis](https://redis.io/download/)
- [Docker](https://www.docker.com/)
- [AWS S3](https://aws.amazon.com/)
- [OpenAPI](https://swagger.io/specification/)

## Getting Started
To use online-gallery API, you'll need to have Java SE 17 installed on your computer. You'll also need to clone this repository and set up a MongoDB and Redis databases. Then fill the application.properties based on your database

## Installation and usage
Clone the repository:

bash
Copy code
git clone https://github.com/Grandench1k/online-gallery.git
Alternatively, download the latest release.

AWS S3 Setup:
Follow this AWS guide to set up your AWS S3 credentials file.

Configure application properties:
Navigate to src/main/resources/ and update application.properties with your specific configurations.

Configuration
The application.properties file houses all the essential configurations for the API. Each property is pivotal for the tailored functionality of the Online Gallery API.

Development Environment Setup
Ensure your development environment is equipped with Java 17, MongoDB, Redis, Docker, and AWS S3. Detailed setup instructions for each can be found on their respective official websites.

Example of API work:

GET /api/v1/images HTTP/1.1
```
Host: example.com
Accept: application/json
Authorization: Bearer access_token
```
Response body
```
{
  "images": [
    {
      "id": "img1",
      "name": "img1",
      "url": "img2.jpg",
      "userId": "user1"
    },
    {
      "id": "img2",
      "name": "img2",
      "url": "img2.png",
      "userId": "user2"
    }
  ]
}
```

## Roadmap
For future enhancements and known issues, refer to our Issues page. Join in by voting for your desired features or reporting new bugs.

## Support
For support queries, please file an issue or reach out via the contact options on my profile.

## Project assistance

If you want to say **thank you** or/and support active development of online-gallery API:

- Add a [GitHub Star](https://github.com/Grandench1k/online-gallery/) to the project.
- share this project with your developer friends.

Together, we can make online-gallery API **better**!

## Contributing
Contributions are what make the open-source community thrive. Refer to our contribution guidelines for more information on how you can contribute.

## Author
Grandench1k - Initial work and ongoing maintenance.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
