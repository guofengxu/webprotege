#!/usr/bin/env python3
"""Validate GitHub branch names and Conventional Commit headers for this repo.

Usage:
  validate_names.py --branch NAME [--message TEXT]
  validate_names.py --message TEXT
  echo "feat(api): add endpoint" | validate_names.py --message -

Exit codes: 0 all checked items pass; 1 otherwise.
"""

from __future__ import annotations

import argparse
import re
import sys

# Ticket: 123-  or  WP-45- / wp-45-  then kebab description
BRANCH_RE = re.compile(
    r"^(feature|fix|hotfix|test|refactor|docs|chore|release)/"
    r"(([A-Za-z]+-[0-9]+|[0-9]+)-)?"
    r"[a-z0-9]+(-[a-z0-9]+)*$"
)

COMMIT_HEADER_RE = re.compile(
    r"^(feat|fix|docs|style|refactor|perf|test|chore|ci|build|revert)"
    r"(\([a-z0-9][a-z0-9./-]*\))?"
    r"(!)?: "
    r"[a-z].{0,71}$"
)

BANNED_SUBJECTS = {"update", "fix", "wip", "updates", "misc", "temp"}

PROTECTED = {"main", "master", "develop", "dev"}


def check_branch(name: str) -> list[str]:
    errors: list[str] = []
    if not name:
        return ["branch name is empty"]
    if name in PROTECTED:
        return [f"refusing to treat protected branch as feature branch: {name}"]
    if not BRANCH_RE.match(name):
        errors.append(
            "branch must match <type>/<optional-ticket>-<kebab-desc>; "
            f"got: {name!r}"
        )
    return errors


def check_message(message: str) -> list[str]:
    errors: list[str] = []
    if message is None:
        return ["commit message is missing"]
    text = message.strip()
    if not text:
        return ["commit message is empty"]
    header = text.splitlines()[0].strip()
    if not COMMIT_HEADER_RE.match(header):
        errors.append(
            "commit header must be Conventional Commits "
            "(type(scope)?: subject, subject lowercase start, no trailing '.'); "
            f"got: {header!r}"
        )
        return errors
    subject = header.split(": ", 1)[1]
    if subject.rstrip(".").lower() in BANNED_SUBJECTS:
        errors.append(f"commit subject is too vague: {subject!r}")
    if header.endswith("."):
        errors.append("commit subject must not end with a period")
    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--branch", help="Branch name to validate")
    parser.add_argument(
        "--message",
        help="Commit message (use '-' to read stdin)",
    )
    args = parser.parse_args()

    if not args.branch and not args.message:
        parser.error("provide --branch and/or --message")

    failures = 0

    if args.branch is not None:
        errs = check_branch(args.branch)
        if errs:
            failures += 1
            print(f"FAIL branch: {args.branch}")
            for e in errs:
                print(f"  - {e}")
        else:
            print(f"OK   branch: {args.branch}")

    if args.message is not None:
        msg = sys.stdin.read() if args.message == "-" else args.message
        errs = check_message(msg)
        if errs:
            failures += 1
            print("FAIL message:")
            for e in errs:
                print(f"  - {e}")
        else:
            header = msg.strip().splitlines()[0]
            print(f"OK   message: {header}")

    return 1 if failures else 0


if __name__ == "__main__":
    sys.exit(main())
