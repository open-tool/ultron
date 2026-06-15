# Release process

Ultron releases are prepared locally and published by CI after a reviewed release PR is merged.

## Authorization model

Merging a valid `release/<version>` pull request into `master` authorizes publication. There is no second manual approval in GitHub Actions.

The release flow is:

1. Run `scripts/prepare-ultron-release.sh <version>` from a clean `master`.
2. Review the generated release PR.
3. Merge the release PR into `master`.
4. CI creates tag `<version>` on the merge commit.
5. CI starts the release workflow for that tag.
6. CI creates the GitHub Release.
7. CI sends the Telegram announcement.

Pushing a `release/*` branch does not publish artifacts.

## Local command

```bash
scripts/prepare-ultron-release.sh 2.6.3
```

Use dry-run mode to preview the file changes and PR body:

```bash
scripts/prepare-ultron-release.sh --dry-run 2.6.3
```

The command updates:

- `gradle.properties`
- `docs/docs/release-notes.md`

The command does not publish Maven artifacts, create Git tags, create GitHub Releases, or send Telegram messages.

## Required GitHub secrets

The release workflow expects these secrets:

- `MAVEN_USER`
- `MAVEN_TOKEN`
- `SIGNING_KEY_ID`
- `SIGNING_PASSWORD`
- `GPG_KEY_CONTENTS`
- `TELEGRAM_BOT_TOKEN`
- `TELEGRAM_CHAT_ID`

The workflow uses `GITHUB_TOKEN` for tag creation, release workflow dispatch, and GitHub Release creation. The workflow permissions grant `contents: write` and `actions: write` where needed.

## Telegram setup

Create a bot with BotFather and add it to the target channel or group before storing the secrets.

1. Open Telegram and start a chat with `@BotFather`.
2. Run `/newbot`, choose a display name and username.
3. Copy the token returned by BotFather into the GitHub secret `TELEGRAM_BOT_TOKEN`.
4. Add the bot to the Telegram channel or group that should receive release announcements.
5. Give the bot permission to post messages. For a channel, add it as an administrator with post permission.
6. Determine the chat ID and store it in `TELEGRAM_CHAT_ID`.

For public channels, `TELEGRAM_CHAT_ID` can usually be the channel username, for example `@ultron_framework`.

For private channels or groups, send any message to the channel/group after adding the bot, then call:

```bash
curl "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/getUpdates"
```

Find the target `chat.id` in the JSON response. Channel and group IDs are often negative numbers, for example `-1001234567890`.

If `getUpdates` returns no channel posts, temporarily message the bot directly or use a small test group to confirm the token, then verify the bot is an administrator in the target channel and that a fresh channel post happened after the bot was added.

Test delivery before the first release:

```bash
curl --fail --request POST "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/sendMessage" \
  --data-urlencode "chat_id=$TELEGRAM_CHAT_ID" \
  --data-urlencode "parse_mode=HTML" \
  --data-urlencode "text=<b>Ultron release test</b>"
```

## Required repository protections

Protect `master` so release PR merge is the publication approval boundary:

- Require pull request review from the release owner.
- Require the `Release PR Guard` status check.
- Require branches to be up to date before merge if that is part of the normal repository policy.
- Do not allow direct pushes to `master`.

Release-sensitive files must require release-owner review through CODEOWNERS or repository rulesets:

- `.github/workflows/**`
- `.github/release.yml`
- `scripts/prepare-ultron-release.sh`
- `scripts/release/**`
- `gradle.properties`
- `docs/docs/release-notes.md`

Protect semver tags with a tag ruleset:

- Target tags matching stable and prerelease release names, for example `*.*.*`.
- Restrict creation and deletion to trusted automation and authorized maintainers.
- Do not allow force updates to existing release tags.

## Release PR requirements

A release PR must:

- use branch `release/<version>`,
- target `master`,
- set `VERSION_NAME=<version>`,
- add `## Version <version>` to `docs/docs/release-notes.md`,
- change only `gradle.properties` and `docs/docs/release-notes.md`,
- reference a tag that does not already exist.

The release notes entry must be reviewed before merge. Replace the generated TODO highlight with human-written release highlights.

## Recovery

### Auto-tag fails

Fix the release metadata in a follow-up PR if the release PR was invalid. If the merge was valid but tag creation failed for an external reason, create the tag manually on the release PR merge commit only after confirming the same validations pass.

### Maven Central publication fails

Do not move or recreate the tag. Fix credentials, signing, or build issues and rerun the failed publish job. If any module was already published successfully, avoid changing the version's source contents.

### GitHub Release creation fails

Do not rerun Maven publication unless publication itself failed. Rerun the GitHub Release job or create the GitHub Release manually from the existing tag using the reviewed release notes.

### Telegram announcement fails

Do not rerun Maven publication. Rerun only the Telegram job or post the announcement manually with the GitHub Release link and Maven coordinates.
