# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.0] - 2025-10-17

### Added

- `kv_storage` subscriptions to key change and key delete events
- New peripheral - Crafka broker!

### Fixed

- A lot of sqlite interaction
- Incorrect expire handling
- Too many jar-in-jar for forge

## [0.2.3] - 2025-10-04

### Fixed

- `incr` now produces non-expirable keys by default

## [0.2.2] - 2025-10-04

### Fixed

- KVStorage works without player online

## [0.2.1] - 2025-09-29

### Added

- `incr` and `decr` for KV storage

## [0.2.0] - 2025-09-22

### Added

- `mget`, `mput` for KV Storage
- Turtle and pocket upgrade support for KV storage and StatsD bridge
- Recieps for blocks, finally!

## [0.1.3] - 2025-03-19

### Fixed

- Dependencies for release

## [0.1.2] - 2025-03-18

### Changed

- New internal library toolkit

## [0.1.1] - 2023-10-02

### Added

- Rate limiting for StatsD bridge
- Limit for values inside KV storage
- Expire check for all KV storage methods

## [0.1.0] - 2023-09-27

### Added

- StatsD bridge
- KV storage
