import React, { useEffect } from "react";
import { BrowserRouter, Routes, Route, Outlet } from "react-router-dom";
import { GlobalContextProviders } from "./components/_globalContextProviders";
import Page_0 from "./pages/team.tsx";
import PageLayout_0 from "./pages/team.pageLayout.tsx";
import Page_1 from "./pages/audio.tsx";
import PageLayout_1 from "./pages/audio.pageLayout.tsx";
import Page_2 from "./pages/login.tsx";
import PageLayout_2 from "./pages/login.pageLayout.tsx";
import Page_3 from "./pages/terms.tsx";
import PageLayout_3 from "./pages/terms.pageLayout.tsx";
import Page_4 from "./pages/users.tsx";
import PageLayout_4 from "./pages/users.pageLayout.tsx";
import Page_5 from "./pages/_index.tsx";
import PageLayout_5 from "./pages/_index.pageLayout.tsx";
import Page_6 from "./pages/privacy.tsx";
import PageLayout_6 from "./pages/privacy.pageLayout.tsx";
import Page_7 from "./pages/register.tsx";
import PageLayout_7 from "./pages/register.pageLayout.tsx";
import Page_8 from "./pages/schedule.tsx";
import PageLayout_8 from "./pages/schedule.pageLayout.tsx";
import Page_9 from "./pages/announcements.tsx";
import PageLayout_9 from "./pages/announcements.pageLayout.tsx";
import Page_10 from "./pages/choose-classes.tsx";
import PageLayout_10 from "./pages/choose-classes.pageLayout.tsx";
import Page_11 from "./pages/forgot-password.tsx";
import PageLayout_11 from "./pages/forgot-password.pageLayout.tsx";

if (!window.requestIdleCallback) {
  window.requestIdleCallback = (cb) => {
    setTimeout(cb, 1);
  };
}

import "./base.css";

const fileNameToRoute = new Map([["./pages/team.tsx","/team"],["./pages/audio.tsx","/audio"],["./pages/login.tsx","/login"],["./pages/terms.tsx","/terms"],["./pages/users.tsx","/users"],["./pages/_index.tsx","/"],["./pages/privacy.tsx","/privacy"],["./pages/register.tsx","/register"],["./pages/schedule.tsx","/schedule"],["./pages/announcements.tsx","/announcements"],["./pages/choose-classes.tsx","/choose-classes"],["./pages/forgot-password.tsx","/forgot-password"]]);
const fileNameToComponent = new Map([
    ["./pages/team.tsx", Page_0],
["./pages/audio.tsx", Page_1],
["./pages/login.tsx", Page_2],
["./pages/terms.tsx", Page_3],
["./pages/users.tsx", Page_4],
["./pages/_index.tsx", Page_5],
["./pages/privacy.tsx", Page_6],
["./pages/register.tsx", Page_7],
["./pages/schedule.tsx", Page_8],
["./pages/announcements.tsx", Page_9],
["./pages/choose-classes.tsx", Page_10],
["./pages/forgot-password.tsx", Page_11],
  ]);

function makePageRoute(filename: string) {
  const Component = fileNameToComponent.get(filename);
  return <Component />;
}

function toElement({
  trie,
  fileNameToRoute,
  makePageRoute,
}: {
  trie: LayoutTrie;
  fileNameToRoute: Map<string, string>;
  makePageRoute: (filename: string) => React.ReactNode;
}) {
  return [
    ...trie.topLevel.map((filename) => (
      <Route
        key={fileNameToRoute.get(filename)}
        path={fileNameToRoute.get(filename)}
        element={makePageRoute(filename)}
      />
    )),
    ...Array.from(trie.trie.entries()).map(([Component, child], index) => (
      <Route
        key={index}
        element={
          <Component>
            <Outlet />
          </Component>
        }
      >
        {toElement({ trie: child, fileNameToRoute, makePageRoute })}
      </Route>
    )),
  ];
}

type LayoutTrieNode = Map<
  React.ComponentType<{ children: React.ReactNode }>,
  LayoutTrie
>;
type LayoutTrie = { topLevel: string[]; trie: LayoutTrieNode };
function buildLayoutTrie(layouts: {
  [fileName: string]: React.ComponentType<{ children: React.ReactNode }>[];
}): LayoutTrie {
  const result: LayoutTrie = { topLevel: [], trie: new Map() };
  Object.entries(layouts).forEach(([fileName, components]) => {
    let cur: LayoutTrie = result;
    for (const component of components) {
      if (!cur.trie.has(component)) {
        cur.trie.set(component, {
          topLevel: [],
          trie: new Map(),
        });
      }
      cur = cur.trie.get(component)!;
    }
    cur.topLevel.push(fileName);
  });
  return result;
}

function NotFound() {
  return (
    <div>
      <h1>Not Found</h1>
      <p>The page you are looking for does not exist.</p>
      <p>Go back to the <a href="/" style={{ color: 'blue' }}>home page</a>.</p>
    </div>
  );
}

import { useLocation, useNavigationType } from "react-router-dom";

export default function ScrollManager() {
  const { pathname, search, hash } = useLocation();
  const navType = useNavigationType(); // "PUSH" | "REPLACE" | "POP"

  useEffect(() => {
    // Back/forward: keep browser-like behavior
    if (navType === "POP") return;

    // Hash links: let the browser scroll to the anchor
    if (hash) return;

    window.scrollTo({ top: 0, left: 0, behavior: "instant" });
  }, [pathname, search, hash, navType]);

  return null;
}

export function App() {
  return (
    <BrowserRouter future={{ v7_startTransition: false, v7_relativeSplatPath: false }}>
      <ScrollManager />
      <GlobalContextProviders>
        <Routes>
          {toElement({ trie: buildLayoutTrie({
"./pages/team.tsx": PageLayout_0,
"./pages/audio.tsx": PageLayout_1,
"./pages/login.tsx": PageLayout_2,
"./pages/terms.tsx": PageLayout_3,
"./pages/users.tsx": PageLayout_4,
"./pages/_index.tsx": PageLayout_5,
"./pages/privacy.tsx": PageLayout_6,
"./pages/register.tsx": PageLayout_7,
"./pages/schedule.tsx": PageLayout_8,
"./pages/announcements.tsx": PageLayout_9,
"./pages/choose-classes.tsx": PageLayout_10,
"./pages/forgot-password.tsx": PageLayout_11,
}), fileNameToRoute, makePageRoute })} 
          <Route path="*" element={<NotFound />} />
        </Routes>
      </GlobalContextProviders>
    </BrowserRouter>
  );
}
