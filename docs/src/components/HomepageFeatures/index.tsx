import Heading from '@theme/Heading';
import Link from '@docusaurus/Link';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  icon: JSX.Element;
  description: string;
  to: string;
};

const iconProps = {
  viewBox: '0 0 24 24',
  fill: 'none',
  stroke: 'currentColor',
  strokeWidth: 1.6,
  strokeLinecap: 'round' as const,
  strokeLinejoin: 'round' as const,
  'aria-hidden': true,
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Page Object built in',
    description:
      'Page and Screen base classes keep locators and user steps in one stateless place, so tests read like scenarios.',
    to: '/docs/',
    icon: (
      <svg {...iconProps}>
        <rect x="3" y="3" width="18" height="18" rx="2.5" />
        <path d="M3 9h18M9 9v12" />
      </svg>
    ),
  },
  {
    title: 'Lists without pain',
    description:
      'RecyclerView and Compose LazyList items are addressed by matcher, index or position — scrolling is handled for you.',
    to: '/docs/compose/lazylist',
    icon: (
      <svg {...iconProps}>
        <path d="M8 6h13M8 12h13M8 18h13" />
        <circle cx="3.6" cy="6" r="1.1" />
        <circle cx="3.6" cy="12" r="1.1" />
        <circle cx="3.6" cy="18" r="1.1" />
      </svg>
    ),
  },
  {
    title: 'Extendable API',
    description:
      'Add your own operations as Kotlin extensions with perform and execute — they inherit retries, logging and reporting.',
    to: '/docs/common/extension',
    icon: (
      <svg {...iconProps}>
        <path d="M14.7 6.3a3.8 3.8 0 0 0 5 5L16 15l-3.5-3.5 2.2-5.2Z" />
        <path d="M12.5 11.5 4.6 19.4a1.9 1.9 0 0 0 2.7 2.7l7.9-7.9" />
      </svg>
    ),
  },
  {
    title: 'Full test lifecycle',
    description:
      'beforeFirstTest, beforeTest and per-test before / go / after blocks — set preconditions for a single test without touching the class.',
    to: '/docs/common/ultrontest',
    icon: (
      <svg {...iconProps}>
        <circle cx="12" cy="12" r="9" />
        <path d="M12 7v5.2l3.4 2" />
      </svg>
    ),
  },
  {
    title: 'Listeners on every step',
    description:
      'Hook into the lifecycle of each operation to log, screenshot or collect anything you need — the same mechanism Allure support uses.',
    to: '/docs/common/listeners',
    icon: (
      <svg {...iconProps}>
        <path d="M4 14v-2a8 8 0 0 1 16 0v2" />
        <rect x="2.5" y="13.5" width="4" height="6" rx="2" />
        <rect x="17.5" y="13.5" width="4" height="6" rx="2" />
      </svg>
    ),
  },
  {
    title: 'Soft assertions',
    description:
      'Collect assertion failures through the test and report them together, instead of stopping at the first one.',
    to: '/docs/common/boolean',
    icon: (
      <svg {...iconProps}>
        <path d="M20 6.5 9.6 17 4 11.4" />
      </svg>
    ),
  },
];

function Feature({title, icon, description, to}: FeatureItem) {
  return (
    <Link to={to} className={styles.card}>
      <span className={styles.icon}>{icon}</span>
      <Heading as="h3" className={styles.cardTitle}>
        {title}
      </Heading>
      <p className={styles.cardDescription}>{description}</p>
    </Link>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className={styles.heading}>
          <span className={styles.eyebrow}>Features</span>
          <Heading as="h2" className={styles.title}>
            An architecture for UI tests, not just a wrapper
          </Heading>
        </div>
        <div className={styles.grid}>
          {FeatureList.map((props) => (
            <Feature key={props.title} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
