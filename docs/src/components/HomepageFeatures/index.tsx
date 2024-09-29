import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  pict: JSX.Element;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Simple',
    pict: (
      <img src={'img/kotlin.png'} alt="simplicity"/> 
    ),
    description: (
      <>
        <p>The simplest syntax for UI tests.</p>
        <code>hasTestTag("elementId").click()</code>
      </>
    ),
  },
  {
    title: 'Stable',
    pict: (
      <img src={'img/stable.webp'} alt="stability"/> 
    ),
    description: (
      <>
        <p>No flaky tests</p>
        <p>Auto-waits for UI elements</p>
        <p>Automatic retries of failed interactions</p>
        <p>Custom assertions of executed action</p>
      </>
    ),
  },
  {
    title: 'Maintainable',
    pict: (
      <img src={'img/maintainability.webp'} alt="maintainability"/> 
    ),
    description: (
      <>
        <p>Page Object support</p>
        <p>Allure support</p>
        <p>Detailed logs</p>
        <p>Extendable API</p>
      </>
    ),
  },
];

function Feature({ title, pict, description }: FeatureItem) {
  const imageStyle = `text--center padding-horiz--md ${styles.imageContainer} `
  return (
    <div className={clsx('col col--4')}>
      <div className='text--center padding-horiz--md'>
        <div className={styles.imageContainer}>
          {pict}
        </div>
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <div>{description}</div>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}