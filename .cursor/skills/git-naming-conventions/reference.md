# Git Naming Conventions — Reference

## Branch name regex

```
^(feature|fix|hotfix|test|refactor|docs|chore|release)/(([A-Za-z]+-[0-9]+|[0-9]+)-)?[a-z0-9]+(-[a-z0-9]+)*$
```

- Optional ticket: `123-` or `WP-45-` / `wp-45-`, then description
- Description: one or more kebab-case segments

## Commit message regex (header)

```
^(feat|fix|docs|style|refactor|perf|test|chore|ci|build|revert)(\([a-z0-9][a-z0-9./-]*\))?(!)?: [a-z].{0,71}$
```

Notes:
- Header type is lowercase
- Scope optional, lowercase
- `!` optional for breaking change
- Subject starts with lowercase letter (imperative), no trailing `.`
- Prefer English subject lines for changelog consistency (Chinese body/PR notes OK)

## Good examples

### Branches

| Scenario | Name |
|----------|------|
| Ontology integration feature | `feature/integration-ontology` |
| Issue 834 login bug | `fix/834-login-redirect` |
| Prod session leak | `hotfix/security-session-leak` |
| Deploy smoke | `test/deployment-readiness` |
| Docs only | `docs/update-docker-readme` |
| Lucene refactor | `refactor/lucene-deprecated-index` |

### Commits

```
feat(search): add Lucene deprecated-entity index

Surface deprecated OWL entities in project search without scanning all axioms.

Closes #812
```

```
fix(auth): prevent session leak on logout

Invalidate the server session before clearing the client cookie so concurrent
requests cannot reuse a half-closed session.

Fixes #834
```

```
chore(docker): pin MongoDB image for reproducible local runs
```

```
docs: document branch and commit naming conventions
```

```
feat(api)!: require API key on /data/projects

BREAKING CHANGE: unauthenticated access to project listing is removed.
```

### PR titles

```
feat(search): add Lucene deprecated-entity index
fix(auth): prevent session leak on logout
chore(docker): pin MongoDB image for reproducible local runs
```

## Anti-patterns

| Bad | Why | Good |
|-----|-----|------|
| `ArchitectureEnhancement` | No type prefix, camelCase | `feature/architecture-enhancement` |
| `feature_add_search` | Underscore, no `/` | `feature/add-search` |
| `Feature/AddSearch` | Uppercase / camelCase | `feature/add-search` |
| `fix` (branch) | Missing description | `fix/834-null-pointer` |
| `update` (commit) | Vague | `chore(deps): bump gson to 2.8.9` |
| `Fixed bug.` | Past tense + period | `fix(ui): correct tab reset confirmation` |
| `WIP` | Not mergeable history | squash before PR, or don't push |

## Mapping from this repo's history

Historical commits often used free-form English (`Updated JVM options...`). Going forward, prefer Conventional Commits. When amending is not allowed, new commits on the branch should still follow the standard; PR title should be conventional even if older commits are mixed.

## Scope suggestions (WebProtégé)

| Scope | Area |
|-------|------|
| `search` | Lucene / short forms |
| `auth` | Login, sessions, API keys |
| `api` | Jersey `/data/*` |
| `client` | GWT UI |
| `server` | Server-core / WAR |
| `docker` | Compose / images |
| `index` | In-memory ontology indices |
| `forms` | Forms subsystem |
| `ci` | GitHub Actions / Maven CI |

Omit scope when the change is cross-cutting or trivial.
