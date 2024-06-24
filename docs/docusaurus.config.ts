import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const config: Config = {
  title: 'Ultron',
  tagline: 'Compose Multiplatform and Android UI testing framework',
  favicon: 'img/favicon.ico',
  url: 'https://ultron.github.io',
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

  themeConfig: {
    // Replace with your project's social card
    image: 'img/docusaurus-social-card.jpg',
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
          label: 'Telegram',
          href: 'https://t.me/ultron_framework',
          position: 'right',
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
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
    algolia: {
          // The application ID provided by Algolia
          appId: 'TLB3E9OO68',

          // Public API key: it is safe to commit it
          apiKey: '06f26f943a74848657b1e5bec4c85aaf',

          indexName: 'open-toolio',

          // Optional: see doc section below
          contextualSearch: true,

          // Optional: Replace parts of the item URLs from Algolia. Useful when using the same search index for multiple deployments using a different baseUrl. You can use regexp or string in the `from` param. For example: localhost:3000 vs myCompany.com/docs
//           replaceSearchResultPathname: {
//             from: '/docs/', // or as RegExp: /\/docs\//
//             to: '/',
//           },

          // Optional: Algolia search parameters
          searchParameters: {},

          // Optional: whether the insights feature is enabled or not on Docsearch (`false` by default)
          insights: false,
        },
    // footer: {
    //   style: 'dark',
    //   links: [
    //     {
    //       title: 'Docs',
    //       items: [
    //         {
    //           label: 'Tutorial',
    //           to: '/docs/intro',
    //         },
    //       ],
    //     },
    //     {
    //       title: 'Community',
    //       items: [
    //         {
    //           label: 'Telegram',
    //           href: 'https://t.me/ultron_framework',
    //         }
    //       ],
    //     },
    //     {
    //       title: 'More',
    //       items: [
    //         {
    //           label: 'Blog',
    //           to: '/blog',
    //         },
    //         {
    //           label: 'GitHub',
    //           href: 'https://github.com/open-tool/ultron',
    //         },
    //       ],
    //     },
    //   ],
    //   copyright: `Copyright © ${new Date().getFullYear()} My Project, Inc. Built with Docusaurus.`,
    // },

    // https://docusaurus.io/blog/2021/11/21/algolia-docsearch-migration#docsearch-has-a-new-home
    // algolia: {
    //   contextualSearch: true,
    // },
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

