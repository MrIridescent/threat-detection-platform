# Contributing to Threat Detection Platform

Thank you for your interest in contributing to the Threat Detection Platform! This document provides guidelines and instructions for contributing.

## Code of Conduct

Please read and follow our [Code of Conduct](CODE_OF_CONDUCT.md).

## How to Contribute

### Reporting Bugs

Bugs are tracked as GitHub issues. Create an issue and provide the following information:

- Use a clear and descriptive title
- Describe the exact steps to reproduce the bug
- Provide specific examples to demonstrate the steps
- Describe the behavior you observed and what you expected to see
- Include screenshots if applicable
- Include any relevant logs

### Suggesting Enhancements

Enhancement suggestions are also tracked as GitHub issues. Provide the following information:

- Use a clear and descriptive title
- Provide a detailed description of the suggested enhancement
- Explain why this enhancement would be useful
- Include any relevant examples or mockups

### Pull Requests

1. Fork the repository
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Make your changes
4. Run tests: `mvn test`
5. Commit your changes: `git commit -m 'Add some feature'`
6. Push to the branch: `git push origin feature/your-feature-name`
7. Submit a pull request

#### Pull Request Guidelines

- Update the README.md with details of changes to the interface, if applicable
- Update the documentation with details of any new features or significant changes
- The PR should work with Java 24
- Include appropriate tests for your changes
- Ensure all tests pass before submitting
- Follow the code style used in the project

## Development Setup

### Prerequisites

- JDK 24
- Maven 3.8+
- PostgreSQL 14+ (or H2 for development)

### Building and Testing

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/threat-detection-platform.git
cd threat-detection-platform

# Build the project
mvn clean install

# Run tests
mvn test

# Run the application
mvn spring-boot:run
```

## Style Guides

### Git Commit Messages

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests liberally after the first line

### Java Style Guide

- Follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Include JavaDoc for public methods and classes
- Keep methods focused on a single responsibility

## License

By contributing, you agree that your contributions will be licensed under the project's license.
