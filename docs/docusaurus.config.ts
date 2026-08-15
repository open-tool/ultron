import fs from 'node:fs';
import path from 'node:path';
import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

/**
 * Keeps the version shown on the landing page in sync with the released one.
 * Falls back to an empty string so the site still builds outside the repo.
 */
function readUltronVersion(): string {
  try {
    const gradleProperties = fs.readFileSync(
      path.resolve(__dirname, '..', 'gradle.properties'),
      'utf8',
    );
    return gradleProperties.match(/^VERSION_NAME=(.+)$/m)?.[1].trim() ?? '';
  } catch {
    return '';
  }
}

const config: Config = {
  title: 'Ultron',
  tagline: 'Compose Multiplatform and Android UI testing framework',
  favicon: 'img/favicon.ico',
  url: 'https://open-tool.github.io',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/ultron/',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'Open-tool', // Usually your GitHub org/user name.
  projectName: 'ultron', // Usually your repo name.

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  customFields: {
    ultronVersion: readUltronVersion(),
  },

  themeConfig: {
    image: 'img/ultron_banner_light.png',
    colorMode: {
      respectPrefersColorScheme: true,
    },
    navbar: {
      title: 'Ultron',
      logo: {
        alt: 'Ultron Logo',
        src: 'img/ultron_full_light.png',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'left',
          label: 'Docs',
        },
        {
          href: 'https://t.me/ultron_framework',
          position: 'right',
          className: 'header-telegram-link',
          'aria-label': 'Telegram',
        },
        {
          href: 'https://github.com/open-tool/ultron',
          position: 'right',
          className: 'header-github-link',
          'aria-label': 'GitHub repository',
        },
        {
          type: 'search',
          position: 'right',
        },
      ],
    },
    algolia: {
      appId: 'TLB3E9OO68',
      apiKey: '06f26f943a74848657b1e5bec4c85aaf',
      indexName: 'open-toolio',
      contextualSearch: true,
      searchParameters: {},
      insights: false,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: ['kotlin', 'groovy'],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {label: 'Introduction', to: '/docs/'},
            {label: 'Connect to project', to: '/docs/intro/connect'},
            {label: 'Configuration', to: '/docs/intro/configuration'},
            {label: 'Release notes', to: '/docs/release-notes'},
          ],
        },
        {
          title: 'Guides',
          items: [
            {label: 'Compose', to: '/docs/compose/'},
            {label: 'Espresso', to: '/docs/android/espress'},
            {label: 'UI Automator', to: '/docs/android/uiautomator'},
            {label: 'Allure report', to: '/docs/common/allure'},
          ],
        },
        {
          title: 'Community',
          items: [
            {label: 'GitHub', href: 'https://github.com/open-tool/ultron'},
            {label: 'Telegram', href: 'https://t.me/ultron_framework'},
            {
              label: 'Maven Central',
              href: 'https://central.sonatype.com/search?q=g:com.atiurin',
            },
            {
              label: 'Issues',
              href: 'https://github.com/open-tool/ultron/issues',
            },
          ],
        },
      ],
      copyright: `Ultron is open source under the Apache 2.0 license. Copyright © ${new Date().getFullYear()} Open-tool.`,
    },
  } satisfies Preset.ThemeConfig,

  plugins: [
    [
      '@docusaurus/plugin-client-redirects',
      {
        fromExtensions: ['html', 'htm'], // /myPage.html -> /myPage
        toExtensions: ['exe', 'zip'], // /myAsset -> /myAsset.zip (if latter exists)
        redirects: [
          {
            to: '/docs/',
            from: '/',
          },
        ],
      },
    ],
  ],
};


export default config;

