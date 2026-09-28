[简体中文](README.md) | **English**

# ZhuaTech Freight Audit

> A source-available enterprise project by [ZhuaTech](https://www.zhuatech.cn/) for supply chain, procurement, order, warehouse, and logistics operations.

ZhuaTech Freight Audit provides a practical, self-hosted foundation for supply chain, procurement, order, warehouse, and logistics operations. It is designed for procurement, planning, warehouse, logistics, supplier, and operations teams, with clear business records, controlled workflows, operational visibility, and auditable actions.

This repository is intended for learning, technical evaluation, and non-commercial collaboration. The included implementation, tests, database resources, and container configuration provide a reproducible starting point for further enterprise adaptation.

**Search topics:** enterprise freight audit, self-hosted freight audit, Java Spring Boot enterprise software, digital transformation.

## Solution Overview

- **Primary users:** Procurement, planning, warehouse, logistics, supplier, and operations teams.
- **Deployment model:** Self-hosted, with container-based local deployment where supported.
- **Governance baseline:** Role-aware operations, validation, approval boundaries, exception handling, and auditability.
- **Production boundary:** Review security, identity, backup, observability, capacity, and compliance controls before production use.

## Business Coverage

- **Tenant Guard** — Manage tenant guard with ownership, validation, and explicit lifecycle states.
- **User** — Coordinate user through controlled workflows and approval gates.
- **Auth Session** — Track auth session metrics, exceptions, deadlines, and follow-up actions.
- **Business Record** — Preserve business record evidence in searchable, traceable operational history.
- **Event** — Expose event in role-aware user and administration workspaces.
- **Idempotency Record** — Connect idempotency record to external systems through configurable integration boundaries.

## Implementation Stack

**Technology stack:** Java 21 · Spring Boot · Vue 3 · Vite · MySQL 8 · Docker Compose

### Repository Layout

- `backend/` — Java backend, domain services, APIs, validation, and automated tests
- `frontend/` — responsive user and administration interfaces
- `docs/` — architecture, operations, screenshots, and supporting documentation
- `scripts/` — repeatable local validation and maintenance scripts
- `compose.yaml` — local multi-service orchestration

## Local Deployment

```bash
docker compose --env-file .env.demo.example up -d --build
```

- The demonstration environment file is for local evaluation only. Do not expose demonstration credentials to the Internet.
- Review `compose.yaml` before changing published ports, storage paths, or production credentials.

## Verification

Run the checks supported by this repository before changing or deploying it:

```bash
sh scripts/test.sh
```

## Interface Preview

### Workbench

![Workbench](docs/images/workbench.png)

### Admin

![Admin](docs/images/admin.png)

### Record Detail

![Record Detail](docs/images/record-detail.png)

### Mobile

![Mobile](docs/images/mobile.png)

## Security and Production Readiness

- Never commit real passwords, API keys, tokens, certificates, customer data, or production connection strings.
- Replace all local demonstration credentials and secrets before deployment.
- Apply least privilege, tenant isolation, backup and restore drills, monitoring, rate limiting, and vulnerability management.
- Please report security issues privately through the contact channels below instead of publishing sensitive details.

## Usage and Commercial Licensing

Copyright © 2026 Shanghai Rujing Zhihua Information Technology Co., Ltd.

This project is a publicly available source edition intended solely for personal learning, technical research, and non-commercial communication. Commercial use, paid delivery, resale, hosted commercial services, and commercial derivative distribution require prior written authorization from the copyright holder.

Third-party dependencies remain subject to their respective licenses. Review the repository `LICENSE` and `NOTICE` files before use.

## Commercial Licensing and Enterprise Services

For commercial licensing, private deployment, enterprise customization, software outsourcing, implementation services, FDE outsourcing, OPC technical support, or AI transformation consulting, contact ZhuaTech:

- Email: [han@zhuatech.cn](mailto:han@zhuatech.cn)
- Email: [jack@zhuatech.cn](mailto:jack@zhuatech.cn)
- [WhatsApp: +86 17521234993](https://wa.me/8617521234993)
- Website: [https://www.zhuatech.cn/](https://www.zhuatech.cn/)

## About ZhuaTech

[ZhuaTech](https://www.zhuatech.cn/) is operated by Shanghai Rujing Zhihua Information Technology Co., Ltd. We support small and medium-sized enterprises with digital transformation, AI adoption, enterprise software implementation, custom development, software project outsourcing, FDE services, OPC integration, and long-term technical support.
