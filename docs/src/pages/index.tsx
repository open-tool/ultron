import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useBaseUrl from '@docusaurus/useBaseUrl';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import Heading from '@theme/Heading';
import CodeBlock from '@theme/CodeBlock';
import ThemedImage from '@theme/ThemedImage';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import HomepageFeatures from '@site/src/components/HomepageFeatures';

import styles from './index.module.css';

const PLATFORM_GROUPS = [
  {
    label: 'Android',
    hint: 'Espresso · UI Automator · Compose',
    targets: ['Views', 'Compose', 'WebView'],
  },
  {
    label: 'Compose Multiplatform',
    hint: 'one test source set',
    targets: ['iOS', 'Desktop', 'Web', 'macOS'],
  },
];

type Comparison = {
  id: string;
  label: string;
  vanillaLabel: string;
  vanilla: string;
  ultron: string;
};

const COMPARISONS: Comparison[] = [
  {
    id: 'compose',
    label: 'Compose',
    vanillaLabel: 'Compose UI test',
    vanilla: `composeTestRule.onNode(hasTestTag("Continue"))
    .performClick()
composeTestRule.onNodeWithText("Welcome")
    .assertIsDisplayed()`,
    ultron: `hasTestTag("Continue").click()
hasText("Welcome").assertIsDisplayed()`,
  },
  {
    id: 'lazylist',
    label: 'LazyList',
    vanillaLabel: 'Compose UI test',
    vanilla: `val itemMatcher = hasText(contact.name)
composeRule
    .onNodeWithTag(contactsListTestTag)
    .performScrollToNode(itemMatcher)
    .onChildren()
    .filterToOne(itemMatcher)
    .assertTextContains(contact.name)`,
    ultron: `composeList(hasTestTag(contactsListTestTag))
    .item(hasText(contact.name))
    .assertTextContains(contact.name)`,
  },
  {
    id: 'espresso',
    label: 'Espresso',
    vanillaLabel: 'Espresso',
    vanilla: `onView(withId(R.id.send_button))
    .check(matches(isDisplayed()))
    .perform(click())`,
    ultron: `withId(R.id.send_button).isDisplayed().click()`,
  },
  {
    id: 'recyclerview',
    label: 'RecyclerView',
    vanillaLabel: 'Espresso',
    vanilla: `onView(withId(R.id.recycler_friends))
    .perform(
        RecyclerViewActions
            .actionOnItem<RecyclerView.ViewHolder>(
                hasDescendant(withText("Janice")),
                click()
            )
    )`,
    ultron: `withRecyclerView(R.id.recycler_friends)
    .item(hasDescendant(withText("Janice")))
    .click()`,
  },
  {
    id: 'uiautomator',
    label: 'UI Automator',
    vanillaLabel: 'UI Automator',
    vanilla: `val device = UiDevice.getInstance(
    InstrumentationRegistry.getInstrumentation()
)
device.findObject(
    By.res("com.atiurin.sampleapp:id", "button1")
).click()`,
    ultron: `byResId(R.id.button1).click()`,
  },
  {
    id: 'webview',
    label: 'WebView',
    vanillaLabel: 'Espresso Web',
    vanilla: `onWebView()
    .withElement(findElement(Locator.ID, "text_input"))
    .perform(webKeys(newTitle))
    .withElement(findElement(Locator.ID, "button1"))
    .perform(webClick())
    .withElement(findElement(Locator.ID, "title"))
    .check(webMatches(getText(), containsString(newTitle)))`,
    ultron: `id("text_input").webKeys(newTitle)
id("button1").webClick()
id("title").hasText(newTitle)`,
  },
];

const ARTIFACTS = [
  {
    name: 'ultron-compose',
    description:
      'Compose UI tests for Android apps and Compose Multiplatform targets.',
    to: '/docs/compose/',
  },
  {
    name: 'ultron-android',
    description:
      'Native Android UI tests on top of Espresso, Espresso Web and UI Automator.',
    to: '/docs/android/espress',
  },
  {
    name: 'ultron-allure',
    description:
      'Allure report artifacts: steps, screenshots and view hierarchy on failure.',
    to: '/docs/common/allure',
  },
];

function useVersion(): string {
  const {siteConfig} = useDocusaurusContext();
  return (siteConfig.customFields?.ultronVersion as string) || 'latest_version';
}

function HomepageHeader() {
  const version = useVersion();
  return (
    <header className={styles.hero}>
      <div className={clsx('container', styles.heroContent)}>
        <ThemedImage
          className={styles.heroLogo}
          alt="Ultron"
          sources={{
            light: useBaseUrl('/img/ultron_banner_light.png'),
            dark: useBaseUrl('/img/ultron_banner_dark.png'),
          }}
        />
        <Heading as="h1" className={styles.heroTitle}>
          UI tests that simply don&apos;t flake
        </Heading>
        <p className={styles.heroSubtitle}>
          A testing framework for <strong>Android</strong> and{' '}
          <strong>Compose Multiplatform</strong>. Espresso, UI Automator and
          Compose UI testing — with a syntax you actually want to write, and
          stability built into every action and assertion.
        </p>

        <div className={styles.heroButtons}>
          <Link className="button button--primary button--lg" to="/docs/">
            Get started
          </Link>
          <Link
            className="button button--secondary button--lg"
            href="https://github.com/open-tool/ultron">
            View on GitHub
          </Link>
          <Link
            className={clsx('button button--secondary button--lg', styles.telegramButton)}
            href="https://t.me/ultron_framework">
            Join the Telegram chat
          </Link>
        </div>

        <div className={styles.heroInstall}>
          <CodeBlock language="kotlin">
            {`androidTestImplementation("com.atiurin:ultron-compose:${version}")`}
          </CodeBlock>
        </div>

        <div className={styles.platforms}>
          {PLATFORM_GROUPS.map((group) => (
            <div key={group.label} className={styles.platformGroup}>
              <span className={styles.platformGroupLabel}>
                {group.label}
                <span className={styles.platformGroupHint}>{group.hint}</span>
              </span>
              <ul className={styles.platformList}>
                {group.targets.map((target) => (
                  <li key={target} className={styles.platform}>
                    {target}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </div>
    </header>
  );
}

function ComparisonSection() {
  return (
    <section className={styles.section}>
      <div className="container">
        <div className={styles.sectionHeading}>
          <span className={styles.eyebrow}>Syntax</span>
          <Heading as="h2" className={styles.sectionTitle}>
            The same test, minus the ceremony
          </Heading>
          <p className={styles.sectionSubtitle}>
            Ultron keeps the operation names you already know and drops
            everything else. No new DSL to learn.
          </p>
        </div>

        <Tabs className={styles.comparisonTabs} groupId="framework">
          {COMPARISONS.map((item) => (
            <TabItem key={item.id} value={item.id} label={item.label}>
              <div className={styles.comparisonGrid}>
                <div className={styles.comparisonColumn}>
                  <div className={styles.comparisonLabel}>
                    {item.vanillaLabel}
                  </div>
                  <CodeBlock language="kotlin">{item.vanilla}</CodeBlock>
                </div>
                <div
                  className={clsx(
                    styles.comparisonColumn,
                    styles.comparisonColumnHighlighted,
                  )}>
                  <div
                    className={clsx(
                      styles.comparisonLabel,
                      styles.comparisonLabelAccent,
                    )}>
                    Ultron
                  </div>
                  <CodeBlock language="kotlin">{item.ultron}</CodeBlock>
                </div>
              </div>
            </TabItem>
          ))}
        </Tabs>
      </div>
    </section>
  );
}

function StabilitySection() {
  return (
    <section className={clsx(styles.section, styles.sectionAlt)}>
      <div className="container">
        <div className={styles.splitGrid}>
          <div>
            <span className={styles.eyebrow}>Stability</span>
            <Heading as="h2" className={styles.sectionTitle}>
              Every operation retries itself
            </Heading>
            <p className={styles.sectionSubtitle}>
              Ultron doesn&apos;t execute an action once and hope for the best.
              It repeats the operation until it succeeds or the timeout expires,
              and only for the exceptions you allow — an unexpected failure
              still fails fast.
            </p>
            <ol className={styles.steps}>
              <li>
                <strong>Execute</strong> the action or assertion.
              </li>
              <li>
                <strong>Analyse</strong> the failure — retry only allowed
                exceptions, abort on the rest.
              </li>
              <li>
                <strong>Assert</strong> the custom result condition, then report
                the whole step with a readable description.
              </li>
            </ol>
            <Link className={styles.textLink} to="/docs/intro/configuration">
              Configure timeouts and allowed exceptions →
            </Link>
          </div>
          <div>
            <CodeBlock language="kotlin" title="Stable by default">
              {`// waits and retries until the text appears (5s by default)
withId(R.id.result).hasText("Passed")

// a slow screen? ask for more time on this operation only
withId(R.id.result)
    .withTimeout(10_000)
    .hasText("Passed")

// any operation as a Boolean — no try/catch needed
val isDisplayed = withId(R.id.button).isSuccess {
    isDisplayed()
}`}
            </CodeBlock>
          </div>
        </div>
      </div>
    </section>
  );
}

function AllureSection() {
  return (
    <section className={styles.section}>
      <div className="container">
        <div className={clsx(styles.splitGrid, styles.splitGridReversed)}>
          <div className={styles.reportFrame}>
            <img
              src={useBaseUrl('/img/report.svg')}
              alt="Allure report generated from Ultron test run"
              loading="lazy"
            />
          </div>
          <div>
            <span className={styles.eyebrow}>Reporting</span>
            <Heading as="h2" className={styles.sectionTitle}>
              Allure report out of the box
            </Heading>
            <p className={styles.sectionSubtitle}>
              Add <code>ultron-allure</code>, apply the recommended config and
              every operation becomes a report step — with screenshots, view
              hierarchy and logs attached on failure. No manual instrumentation.
            </p>
            <CodeBlock language="kotlin">
              {`@BeforeClass
@JvmStatic
fun setConfig() {
    UltronConfig.applyRecommended()
    UltronAllureConfig.applyRecommended()
    UltronComposeConfig.applyRecommended()
}`}
            </CodeBlock>
            <Link className={styles.textLink} to="/docs/common/allure">
              Read the Allure guide →
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
}

function ArtifactsSection() {
  const version = useVersion();
  return (
    <section className={clsx(styles.section, styles.sectionAlt)}>
      <div className="container">
        <div className={styles.sectionHeading}>
          <span className={styles.eyebrow}>Get started</span>
          <Heading as="h2" className={styles.sectionTitle}>
            Three libraries, pick what you need
          </Heading>
          <p className={styles.sectionSubtitle}>
            Published to Maven Central. Current version{' '}
            <code>{version}</code>.
          </p>
        </div>

        <div className={styles.artifactGrid}>
          {ARTIFACTS.map((artifact) => (
            <Link
              key={artifact.name}
              to={artifact.to}
              className={styles.artifactCard}>
              <span className={styles.artifactName}>{artifact.name}</span>
              <span className={styles.artifactDescription}>
                {artifact.description}
              </span>
            </Link>
          ))}
        </div>

        <div className={styles.installBlock}>
          <CodeBlock language="kotlin" title="build.gradle.kts">
            {`dependencies {
    androidTestImplementation("com.atiurin:ultron-compose:${version}")
    androidTestImplementation("com.atiurin:ultron-android:${version}")
    androidTestImplementation("com.atiurin:ultron-allure:${version}")
}`}
          </CodeBlock>
        </div>

        <div className={styles.finalCta}>
          <Link className="button button--primary button--lg" to="/docs/">
            Read the docs
          </Link>
          <Link
            className="button button--secondary button--lg"
            to="/docs/intro/connect">
            Connect to your project
          </Link>
        </div>
      </div>
    </section>
  );
}

export default function Home(): JSX.Element {
  return (
    <Layout
      title="UI tests that simply don't flake"
      description="Ultron is a UI testing framework for Android and Compose Multiplatform, built on Espresso, UI Automator and Compose UI testing. Simple syntax, stable operations, Allure reports out of the box.">
      <HomepageHeader />
      <main>
        <ComparisonSection />
        <HomepageFeatures />
        <StabilitySection />
        <AllureSection />
        <ArtifactsSection />
      </main>
    </Layout>
  );
}
