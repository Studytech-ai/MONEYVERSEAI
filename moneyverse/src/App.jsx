import React, { useEffect, useMemo, useState } from "react";
import { ART, ICONS } from "./moneyverseAssets";
import "./App.css";

const INITIAL = {
  points: 250, balance: 380, week: 50,
  goal: { name: "Cycle", saved: 320, target: 500, days: 2, done: false },
  acts: [
    { t: "Pocket Money from Parent", w: "Today, 9:00 AM", a: 500 },
    { t: "Ice Cream", w: "Yesterday, 6:20 PM", a: -50 },
    { t: "Toy", w: "2 days ago", a: -100 }
  ],
  reqs: [], read: [], parent: false
};
const STORIES = [
  { t: "The Wise Saver", d: "Learn how Riya saved for her dream trip!", b: "Riya wanted to visit a sunny island. Every week she put a little pocket money in her jar instead of spending it all on sweets. Some days it was hard to say no, but she kept her dream in mind. After a few months her jar was full, and she boarded the plane with a big smile. Small savings, added up, make big dreams come true!" },
  { t: "The Lemonade Stand", d: "Arjun learns that earning feels great!", b: "Arjun set up a little lemonade stand on a hot afternoon. He counted his costs, set a fair price and smiled at every customer. At the end of the day he saved half of his earnings and spent the rest. He learned that earning, saving and spending wisely all go together." },
  { t: "Need or Want?", d: "Meera figures out what really matters.", b: "Meera saw a shiny gadget and wanted it right away. Her mother asked, “Is it a need or a want?” Meera paused, made a list, and decided to wait one week. After a week she no longer wanted it, and she kept her money for something she truly loved." }
];
const TIPS = ["Save first, spend later.", "Wait 24 hours before buying something you want.", "Ask: is it a need or a want?", "Set a goal and watch it grow.", "Small savings every week add up fast.", "Compare prices before you buy."];
const NAV = [["home","Home",229],["money","My Money",294],["goals","Savings Goals",353],["req","Purchase Requests",412],["stories","Money Stories",468],["rewards","Rewards",525],["settings","Settings",583]];
const money = n => "₹" + Number(n || 0).toLocaleString("en-IN");
const clone = value => JSON.parse(JSON.stringify(value));

function readSaved() {
  try {
    const saved = JSON.parse(localStorage.getItem("mv2") || "null");
    if (saved && typeof saved.balance === "number" && saved.goal) return { ...clone(INITIAL), ...saved };
  } catch {}
  return clone(INITIAL);
}

export default function App() {
  const [data, setData] = useState(readSaved);
  const [view, setView] = useState("home");
  const [modal, setModal] = useState(null);
  const [toast, setToast] = useState("");
  const [menuOpen, setMenuOpen] = useState(false);
  const [scale, setScale] = useState(1);
  const [isMobile, setIsMobile] = useState(() => window.matchMedia("(max-width: 760px)").matches);
  const [machineWeekly, setMachineWeekly] = useState(50);
  const [machineWeeks, setMachineWeeks] = useState(52);
  const [kidName, setKidName] = useState("Kid A");

  useEffect(() => {
    try { localStorage.setItem("mv2", JSON.stringify(data)); } catch {}
  }, [data]);

  useEffect(() => {
    const resize = () => {
      setIsMobile(window.matchMedia("(max-width: 760px)").matches);
      setScale(Math.min(window.innerWidth / 1672, window.innerHeight / 941));
    };
    resize();
    window.addEventListener("resize", resize);
    return () => window.removeEventListener("resize", resize);
  }, []);

  useEffect(() => {
    if (!toast) return;
    const timer = window.setTimeout(() => setToast(""), 2400);
    return () => window.clearTimeout(timer);
  }, [toast]);

  const goal = data.goal;
  const progress = Math.min(100, Math.round((goal.saved / Math.max(1, goal.target)) * 100));
  const badges = useMemo(() => [
    ["🌱","First Saver",goal.saved > 0],
    ["🚴","Goal Getter",goal.done || goal.saved >= goal.target],
    ["🛍️","Smart Spender",data.reqs.length > 0],
    ["📖","Story Lover",data.read.length > 0],
    ["⭐","Point Master",data.points >= 500],
    ["💰","Rich Kid",data.balance >= 1000]
  ], [goal, data.reqs.length, data.read.length, data.points, data.balance]);

  const notify = message => setToast(message);
  const navigate = next => {
    if (data.parent && next !== "parent") return notify("Switch to Kid mode first");
    setView(next);
    setMenuOpen(false);
  };
  const update = updater => setData(current => typeof updater === "function" ? updater(current) : updater);
  const addTransaction = (title, amount, points = 0) => update(current => ({
    ...current,
    acts: [{ t: title, w: "Just now", a: amount }, ...current.acts],
    balance: current.balance + amount,
    points: current.points + points
  }));
  const openModal = (kind, title, fields) => setModal({ kind, title, fields, values: fields.map(f => f.value || "") });
  const changeField = (index, value) => setModal(current => ({
    ...current, values: current.values.map((v, i) => i === index ? value : v)
  }));

  const submitModal = () => {
    if (!modal) return;
    const values = modal.values;
    const amount = Number(values[0]);
    if (modal.kind === "addGoal") {
      if (!(amount > 0)) return notify("Enter a valid amount");
      if (amount > data.balance) return notify("Not enough balance");
      if (amount > goal.target - goal.saved) return notify(`Only ${money(goal.target - goal.saved)} needed`);
      const saved = goal.saved + amount;
      const completed = saved >= goal.target && !goal.done;
      update(current => ({
        ...current, balance: current.balance - amount, points: current.points + 10 + (completed ? 100 : 0),
        goal: { ...current.goal, saved, done: current.goal.done || completed },
        acts: [{ t: `Saved for ${goal.name}`, w: "Just now", a: -amount }, ...current.acts]
      }));
      setModal(null);
      notify(completed ? "🎉 Goal reached! +100 points" : `Saved ${money(amount)}! +10 points`);
    } else if (modal.kind === "newGoal") {
      if (!values[0]?.trim() || !(Number(values[1]) > 0)) return notify("Fill name and target");
      update(current => ({ ...current, goal: { name: values[0].trim(), saved: 0, target: Number(values[1]), days: Number(values[2]) || 30, done: false } }));
      setModal(null); notify("New savings goal created!");
    } else if (modal.kind === "spend") {
      const n = Number(values[1]);
      if (!values[0]?.trim() || !(n > 0)) return notify("Fill both fields");
      if (n > data.balance) return notify("Not enough balance");
      addTransaction(values[0].trim(), -n);
      setModal(null); notify("Purchase recorded");
    } else if (modal.kind === "padd") {
      if (!(amount > 0)) return notify("Enter a valid amount");
      update(current => ({ ...current, balance: current.balance + amount, week: current.week + amount,
        acts: [{ t: "Pocket Money from Parent", w: "Just now", a: amount }, ...current.acts] }));
      setModal(null); notify(`Added ${money(amount)}`);
    }
  };

  const toggleParent = () => {
    update(current => ({ ...current, parent: !current.parent }));
    setView(data.parent ? "home" : "parent");
    setMenuOpen(false);
    notify(data.parent ? "Back to Kid Mode" : "Parent Mode on");
  };
  const sendRequest = (name, price) => {
    const n = name.trim(), p = Number(price);
    if (!n || !(p > 0)) return notify("Enter item and price");
    update(current => ({ ...current, reqs: [{ n, p, s: "p" }, ...current.reqs], points: current.points + 5 }));
    notify("Request sent to parents! +5 points");
  };
  const approveRequest = index => {
    const request = data.reqs[index];
    if (!request || request.s !== "p") return;
    if (request.p > data.balance) return notify("Balance too low");
    update(current => ({
      ...current, balance: current.balance - request.p,
      reqs: current.reqs.map((r, i) => i === index ? { ...r, s: "a" } : r),
      acts: [{ t: request.n, w: "Just now", a: -request.p }, ...current.acts]
    }));
    notify("Request approved");
  };
  const declineRequest = index => update(current => ({
    ...current, reqs: current.reqs.map((r, i) => i === index ? { ...r, s: "d" } : r)
  }));
  const finishStory = () => {
    const index = data.currentStory ?? 0;
    const isNew = !data.read.includes(index);
    update(current => ({ ...current, read: isNew ? [...current.read, index] : current.read, points: current.points + (isNew ? 20 : 0) }));
    setView("stories");
    if (isNew) notify("Story completed! +20 points");
  };
  const reset = () => {
    if (window.confirm("Reset all MoneyVerse data on this device?")) {
      update(clone(INITIAL)); setView("home"); setModal(null); notify("Demo data restored");
    }
  };

  const image = (key, x, y, w, h, extra = {}) => <img className="mv-art" src={ART[key]} alt="" style={{ left: x, top: y, width: w, height: h, ...extra }} />;
  const text = (x, y, size, color, children, extra = {}) => <div className="mv-abs" style={{ left: x, top: y, fontSize: size, color, ...extra }}>{children}</div>;
  const box = (x, y, w, h, className, style = {}, onClick, children) =>
    <div className={className} onClick={onClick} style={{ position: "absolute", left: x, top: y, width: w, height: h, ...style }}>{children}</div>;
  const requestRows = () => data.reqs.length ? data.reqs.map((r, i) =>
    <div className="mv-list-row" key={`${r.n}-${i}`}>
      <span>{r.n} — {money(r.p)}</span>
      <span className={`mv-tag ${r.s === "a" ? "ok" : r.s === "d" ? "no" : ""}`}>{r.s === "p" ? "Pending" : r.s === "a" ? "Approved" : "Declined"}</span>
      {data.parent && r.s === "p" && <><button className="mv-btn green" onClick={() => approveRequest(i)}>Approve</button><button className="mv-btn red" onClick={() => declineRequest(i)}>Decline</button></>}
    </div>) : <div className="mv-list-row"><span>No requests yet.</span></div>;

  const home = <div className="mv-home">
    {image("hero",248,0,1424,392)}
    {text(318,36,54,"#2a1b5e",<>Hello, {kidName}! 👋</>,{fontWeight:800})}
    {text(318,102,20,"#2a1b5e","Ready to make smart money choices today?")}
    <svg className="mv-art" style={{left:440,top:136}} width="200" height="14" viewBox="0 0 200 14"><path d="M2 10 Q60 2 198 6" stroke="#f4a21b" strokeWidth="3" fill="none" strokeLinecap="round"/></svg>
    {box(1328,24,175,58,"mv-points",{},()=>navigate("rewards"),<>{image("sun",14,12,42,38)}<span>{data.points} Points</span></>)}
    {box(1525,24,128,60,"mv-profile",{},()=>setMenuOpen(v=>!v),<>{image("face",2,0,63,60,{borderRadius:"50%"})}<span>⌄</span></>)}
    {box(285,167,353,192,"mv-balance",{},()=>navigate("money"),<>{image("wallet",37,15,90,86)}<div className="mv-balance-label">Your Balance</div><div className="mv-balance-value">{money(data.balance)}</div><div className="mv-week">↑ +{money(data.week)} this week</div></>)}
    {box(268,380,444,285,"mv-goal-card",{},()=>openModal("addGoal",`Add to ${goal.name} goal`,[{placeholder:"Amount (₹)",type:"number"}]),<>{image("bike",4,56,198,224)}{image("target",16,14,56,52)}<div className="mv-goal-title">Your Savings Goal</div><button className="mv-view-all" onClick={e=>{e.stopPropagation();navigate("goals")}}>View All →</button><div className="mv-goal-name">{goal.name}</div><div className="mv-goal-amount">{money(goal.saved)} / {money(goal.target)}</div><div className="mv-progress"><i style={{width:`${progress}%`}}/></div><div className="mv-progress-number">{progress}%</div><div className="mv-goal-days">🕒 {goal.saved>=goal.target?"Goal reached!":`${goal.days} days left!`}</div></>)}
    {box(728,380,414,288,"mv-activity-card",{},null,<>{image("clock",20,14,40,42)}<div className="mv-activity-title">Recent Activity</div><button className="mv-view-all activity-view" onClick={()=>navigate("money")}>View All →</button>{data.acts.slice(0,3).map((a,i)=><div className="mv-activity-row" key={`${a.t}-${i}`} style={{top:67+i*70}}><span className={`mv-activity-icon ${a.a>0?"income":a.t==="Toy"||a.t.startsWith("Saved")?"saving":"expense"}`}>{a.a>0?"+":a.t==="Toy"?"🎁":a.t.startsWith("Saved")?"🎯":"−"}</span><span className="mv-activity-copy">{a.t}<small>{a.w}</small></span><b className={a.a>0?"positive":"negative"}>{a.a>0?"+":"-"}{money(Math.abs(a.a))}</b></div>)}</>)}
    {box(1160,380,492,303,"mv-story-card",{},()=>navigate("stories"),<>{image("story",205,8,285,288)}{image("book",22,18,54,42)}<div className="mv-story-heading">Money Storybook</div><div className="mv-story-highlight">The Story of<br/>The Wise Saver</div><div className="mv-story-description">Learn how Riya saved<br/>for her dream trip!</div></>)}
    {image("star",268,684,40,40)}
    {text(318,690,27,"#2a1b5e","Explore More")}
    {[
      {x:282,w:246,bg:"linear-gradient(135deg,#aef0d4,#d8f7ea)",art:"ti1",artW:135,artH:102,title:<>Money<br/>Time Machine</>,sub:"See the future!",view:"machine",go:"#16a34a",titleTop:88,subTop:142},
      {x:545,w:235,bg:"linear-gradient(135deg,#c2e8ff,#ddd3ff)",art:"ti2",artW:160,artH:102,title:"Rewards",sub:"Earn badges!",view:"rewards",go:"#2b7fff",titleTop:106,subTop:134},
      {x:798,w:235,bg:"linear-gradient(135deg,#ffc7d9,#ffe3ec)",art:"ti3",artW:145,artH:102,title:<>Purchase<br/>Requests</>,sub:"Ask your parents!",view:"req",go:"#ef3b4c",titleTop:88,subTop:142},
      {x:1052,w:257,bg:"linear-gradient(135deg,#dac8ff,#f0e8ff)",art:"ti4",artW:150,artH:80,title:"Money Tips",sub:"Be a smart spender!",view:"tips",go:"#6d3df0",titleTop:106,subTop:134}
    ].map(tile => box(tile.x,730,tile.w,172,"mv-tile",{background:tile.bg},()=>navigate(tile.view),<>{image(tile.art,53,0,tile.artW,tile.artH)}<div className="mv-tile-title" style={{top:tile.titleTop}}>{tile.title}</div><div className="mv-tile-sub" style={{top:tile.subTop}}>{tile.sub}</div><span className="mv-tile-go" style={{background:tile.go}}>→</span></>))}
    {box(1342,712,312,196,"mv-parent-card",{},null,<>{image("pav",26,16,84,80,{borderRadius:"50%"})}<div className="mv-parent-title">Parent Mode</div><div className="mv-parent-copy">Manage your kid’s<br/>money journey.</div><button className="mv-parent-button" onClick={toggleParent}>Switch to Parent →</button></>)}
  </div>;

  const pageShell = content => isMobile
    ? <div className="mv-mobile-page"><button className="mv-mobile-back" onClick={()=>navigate("home")}>← Back to dashboard</button>{content}</div>
    : <div className="mv-page"><button className="mv-btn light" onClick={()=>navigate("home")}>← Home</button>{content}</div>;
  const page = (() => {
    if (view === "money") return pageShell(<><h2>My Money</h2><p className="mv-sub">Your Balance: <b>{money(data.balance)}</b> · <span className="positive">+{money(data.week)} this week</span></p><button className="mv-btn" onClick={()=>openModal("spend","Spend Money",[{placeholder:"What did you buy?"},{placeholder:"Amount (₹)",type:"number"}])}>Spend Money</button><div className="mv-list mv-card">{data.acts.map((a,i)=><div className="mv-list-row" key={`${a.t}-${i}`}><span>{a.t}<small>{a.w}</small></span><b className={a.a>0?"positive":"negative"}>{a.a>0?"+":"-"}{money(Math.abs(a.a))}</b></div>)}</div></>);
    if (view === "goals") return pageShell(<><h2>Savings Goals</h2><div className="mv-card"><div className="mv-goal-page-name">🚲 {goal.name}</div><p className="mv-goal-page-amount">{money(goal.saved)} / {money(goal.target)} · {progress}%</p><div className="mv-progress wide"><i style={{width:`${progress}%`}}/></div><p className="mv-sub">{goal.saved>=goal.target?"🎉 Goal reached! Great job!":`${money(goal.target-goal.saved)} to go · ${goal.days} days left!`}</p><button className="mv-btn green" onClick={()=>openModal("addGoal",`Add to ${goal.name} goal`,[{placeholder:"Amount (₹)",type:"number"}])}>Add to Goal</button><button className="mv-btn" onClick={()=>openModal("newGoal","New Goal",[{placeholder:"Goal name"},{placeholder:"Target (₹)",type:"number"},{placeholder:"Days to go",type:"number",value:"30"}])}>New Goal</button></div></>);
    if (view === "req") return pageShell(<><h2>Purchase Requests</h2><p className="mv-sub">Ask your parents!</p><RequestForm onSend={sendRequest}/><div className="mv-list mv-card">{requestRows()}</div></>);
    if (view === "stories") return pageShell(<><h2>Money Stories</h2><div className="mv-list mv-card">{STORIES.map((story,i)=><div className="mv-list-row" key={story.t}><span><b>{story.t}</b><small>{story.d}</small></span>{data.read.includes(i)&&<span className="mv-tag ok">Read ✓</span>}<button className="mv-btn" onClick={()=>{update(current=>({...current,currentStory:i}));setView("story")}}>Read</button></div>)}</div></>);
    if (view === "story") { const story=STORIES[data.currentStory ?? 0]; return pageShell(<><h2>{story.t}</h2><div className="mv-card"><p className="mv-story-body">{story.b}</p><button className="mv-btn green" onClick={finishStory}>Finish Story</button></div></>); }
    if (view === "rewards") return pageShell(<><h2>Rewards</h2><p className="mv-sub">Earn badges! You have {data.points} points.</p><div className="mv-badges">{badges.map(b=><div className={`mv-badge ${b[2]?"":"off"}`} key={b[1]}><div>{b[0]}</div>{b[1]}<small>{b[2]?"Unlocked!":"Locked"}</small></div>)}</div></>);
    if (view === "tips") return pageShell(<><h2>Money Tips</h2><p className="mv-sub">Be a smart spender!</p><div className="mv-list mv-card">{TIPS.map(t=><div className="mv-list-row" key={t}><span>💡 {t}</span></div>)}</div></>);
    if (view === "machine") return pageShell(<><h2>Money Time Machine</h2><p className="mv-sub">See the future!</p><div className="mv-form"><input type="number" min="0" value={machineWeekly} onChange={e=>setMachineWeekly(e.target.value)} placeholder="₹ per week"/><select value={machineWeeks} onChange={e=>setMachineWeeks(Number(e.target.value))}><option value="4">1 month</option><option value="26">6 months</option><option value="52">1 year</option><option value="104">2 years</option></select></div><div className="mv-card mv-machine-result">Saving {money(machineWeekly)} a week → <b className="positive">{money(data.balance+(Number(machineWeekly)||0)*machineWeeks)}</b></div></>);
    if (view === "settings") return pageShell(<><h2>Settings</h2><div className="mv-card"><p className="mv-sub">Your progress is saved on this device.</p><button className="mv-btn red" onClick={reset}>Reset Everything</button></div></>);
    if (view === "parent") return pageShell(<><h2>Parent Mode</h2><p className="mv-sub">Manage your kid’s money journey.</p><div className="mv-card">Balance {money(data.balance)} · Goal {goal.name} {money(goal.saved)}/{money(goal.target)}<div className="mv-action-row"><button className="mv-btn green" onClick={()=>openModal("padd","Add Pocket Money",[{placeholder:"Amount (₹)",type:"number"}])}>Add Pocket Money</button><button className="mv-btn" onClick={()=>openModal("newGoal","Set Goal",[{placeholder:"Goal name"},{placeholder:"Target (₹)",type:"number"},{placeholder:"Days to go",type:"number",value:"30"}])}>Set Goal</button><button className="mv-btn light" onClick={toggleParent}>Switch to Kid</button></div></div><h3>Purchase Requests</h3><div className="mv-list mv-card">{requestRows()}</div></>);
    return null;
  })();

  const mobileNav = [
    ["home","Home","⌂"],["money","Wallet","₹"],["goals","Goals","◎"],["stories","Stories","▤"],["rewards","Rewards","★"]
  ];
  const mobileHome = <>
    <section className="mv-mobile-welcome"><div><span className="mv-mobile-eyebrow">YOUR MONEY ADVENTURE</span><h1>Hello, {kidName}! 👋</h1><p>Every smart choice takes you one step further.</p></div><div className="mv-mobile-points" onClick={()=>navigate("rewards")}>⭐ {data.points}<small>POINTS</small></div></section>
    <button className="mv-mobile-balance" onClick={()=>navigate("money")}><span>YOUR TOTAL BALANCE</span><strong>{money(data.balance)}</strong><small>＋ {money(data.week)} this week <b>View wallet →</b></small></button>
    <section className="mv-mobile-section-head"><div><span>YOUR NEXT BIG THING</span><h2>Saving for a dream</h2></div><button onClick={()=>navigate("goals")}>See all →</button></section>
    <button className="mv-mobile-goal" onClick={()=>navigate("goals")}><div className="mv-mobile-goal-icon">🎯</div><div className="mv-mobile-goal-info"><b>{goal.name}</b><span>{money(goal.saved)} saved of {money(goal.target)}</span><div className="mv-mobile-progress"><i style={{width:`${progress}%`}}/></div><small>{progress}% complete · {goal.days} days left</small></div><strong>→</strong></button>
    <section className="mv-mobile-section-head"><div><span>YOUR MONEY TRAIL</span><h2>Recent activity</h2></div><button onClick={()=>navigate("money")}>See all →</button></section>
    <div className="mv-mobile-activity">{data.acts.slice(0,3).map((a,i)=><div className="mv-mobile-activity-row" key={`${a.t}-${i}`}><div className={`mv-mobile-activity-icon ${a.a>0?"in":"out"}`}>{a.a>0?"＋":"−"}</div><div><b>{a.t}</b><small>{a.w}</small></div><strong className={a.a>0?"positive":"negative"}>{a.a>0?"+":"−"}{money(Math.abs(a.a))}</strong></div>)}</div>
    <section className="mv-mobile-section-head"><div><span>LEARN & PLAY</span><h2>Explore MoneyVerse</h2></div></section>
    <div className="mv-mobile-explore"><button className="mint" onClick={()=>navigate("machine")}><span>⏳</span><b>Money Time Machine</b><small>See your savings future</small><i>↗</i></button><button className="blue" onClick={()=>navigate("rewards")}><span>🏆</span><b>Rewards</b><small>Collect your badges</small><i>↗</i></button><button className="pink" onClick={()=>navigate("req")}><span>🛍️</span><b>Purchase Requests</b><small>Ask your parents</small><i>↗</i></button><button className="lavender" onClick={()=>navigate("tips")}><span>💡</span><b>Money Tips</b><small>Become a smart spender</small><i>↗</i></button><button className="peach" onClick={()=>navigate("stories")}><span>📚</span><b>Money Storybook</b><small>Stories that teach</small><i>↗</i></button><button className="cream" onClick={toggleParent}><span>👨‍👩‍👧</span><b>Parent Mode</b><small>Manage your journey</small><i>↗</i></button></div>
  </>;

  if (isMobile) return <div className="mv-mobile-shell">
    <header className="mv-mobile-header"><div className="mv-mobile-brand"><span className="mv-mobile-brand-mark">M</span><span>Money<span>Verse</span></span></div><button className="mv-mobile-avatar" onClick={()=>setMenuOpen(v=>!v)} aria-label="Open menu">{kidName.slice(0,1).toUpperCase()}</button></header>
    {menuOpen && <div className="mv-mobile-menu"><button onClick={()=>navigate("settings")}>⚙ Settings</button><button onClick={()=>navigate("rewards")}>★ My Rewards</button><button onClick={toggleParent}>⇄ Switch to Parent</button></div>}
    <main className="mv-mobile-content">{view==="home"?mobileHome:page}</main>
    <nav className="mv-mobile-bottom-nav" aria-label="Main navigation">{mobileNav.map(([id,label,ico])=><button key={id} className={view===id || (id==="stories"&&view==="story")?"active":""} onClick={()=>navigate(id)}><span>{ico}</span><small>{label}</small></button>)}</nav>
    {modal && <div className="mv-modal-backdrop mv-mobile-modal-layer" onMouseDown={e=>{if(e.target===e.currentTarget)setModal(null)}}><div className="mv-modal"><h3>{modal.title}</h3>{modal.fields.map((field,i)=><input key={i} type={field.type||"text"} min={field.type==="number"?"0":undefined} placeholder={field.placeholder} value={modal.values[i]} onChange={e=>changeField(i,e.target.value)} onKeyDown={e=>{if(e.key==="Enter")submitModal()}}/>)}<div className="mv-modal-actions"><button className="mv-btn" onClick={submitModal}>Confirm</button><button className="mv-btn light" onClick={()=>setModal(null)}>Cancel</button></div></div></div>}
    <div className={`mv-mobile-toast ${toast?"show":""}`}>{toast}</div>
  </div>;

  return <div className="mv-viewport"><div className="mv-stage-wrap" style={{width:1672*scale,height:941*scale}}><main className="mv-stage" style={{transform:`scale(${scale})`}}>
    <img className="mv-sidebar-art" src={ART.side} alt=""/>
    {NAV.map((item,i)=><button key={item[0]} className={`mv-nav ${view===item[0] || (item[0]==="stories"&&view==="story")?"active":""}`} style={{top:item[2]-28}} onClick={()=>navigate(item[0])}><img src={ICONS[i]} alt=""/>{item[1]}</button>)}
    {view==="home" && home}
    {view!=="home" && page}
    {menuOpen && <div className="mv-menu"><button onClick={()=>navigate("settings")}>Settings</button><button onClick={()=>navigate("rewards")}>My Rewards</button><button onClick={toggleParent}>Switch to Parent</button></div>}
    {modal && <div className="mv-modal-backdrop" onMouseDown={e=>{if(e.target===e.currentTarget)setModal(null)}}><div className="mv-modal"><h3>{modal.title}</h3>{modal.fields.map((field,i)=><input key={i} type={field.type||"text"} min={field.type==="number"?"0":undefined} placeholder={field.placeholder} value={modal.values[i]} onChange={e=>changeField(i,e.target.value)} onKeyDown={e=>{if(e.key==="Enter")submitModal()}}/>)}<div className="mv-modal-actions"><button className="mv-btn" onClick={submitModal}>Confirm</button><button className="mv-btn light" onClick={()=>setModal(null)}>Cancel</button></div></div></div>}
    <div className={`mv-toast ${toast?"show":""}`}>{toast}</div>
  </main></div></div>;
}

function RequestForm({ onSend }) {
  const [name,setName] = useState("");
  const [price,setPrice] = useState("");
  return <form className="mv-form" onSubmit={e=>{e.preventDefault();onSend(name,price);setName("");setPrice("");}}>
    <input value={name} onChange={e=>setName(e.target.value)} placeholder="What do you want?" aria-label="Requested item"/>
    <input value={price} onChange={e=>setPrice(e.target.value)} type="number" min="1" placeholder="Price (₹)" aria-label="Price"/>
    <button className="mv-btn">Send Request</button>
  </form>;
}
