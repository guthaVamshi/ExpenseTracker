import { useEffect, useMemo, useState } from 'react'
import type { Expense } from '../lib/api'
import { ExpenseAPI } from '../lib/api'
import { useAuth } from '../store/auth'
import { Area, AreaChart, CartesianGrid, Legend, Tooltip, XAxis, YAxis, ResponsiveContainer } from 'recharts'

export default function DashboardPage() {
  const logout = useAuth((s) => s.logout)
  const [items, setItems] = useState<Expense[]>([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState<Expense>({ expense: '', expenseType: 'Expense', expenseAmount: '', paymentMethod: 'Cash', date: '' })
  const [month, setMonth] = useState<string>('')
  const [query, setQuery] = useState('')
  const [filterMode, setFilterMode] = useState<'all' | 'monthly'>('all')
  const [page, setPage] = useState(1)
  const [pageSize, setPageSize] = useState(10)
  const todayStr = useMemo(() => new Date().toISOString().slice(0, 10), [])

  const load = async () => {
    setLoading(true)
    try {
      const data = await ExpenseAPI.list()
      setItems(data)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  useEffect(() => {
    // default new form date to today
    setForm((f) => ({ ...f, date: f.date && f.date.length > 0 ? f.date : todayStr }))
  }, [todayStr])

  useEffect(() => {
    const fetchByMonth = async () => {
      if (filterMode === 'monthly' && month) {
        setLoading(true)
        try {
          const data = await ExpenseAPI.listByMonth(month)
          setItems(data)
        } finally {
          setLoading(false)
        }
      } else {
        // fallback to all when not monthly or no month selected
        load()
      }
    }
    fetchByMonth()
  }, [filterMode, month])

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (form.id) {
      await ExpenseAPI.update(form)
    } else {
      await ExpenseAPI.add(form)
    }
    setForm({ expense: '', expenseType: 'Expense', expenseAmount: '', paymentMethod: 'Cash', date: todayStr })
    await load()
  }

  const onDelete = async (id?: number) => {
    if (!id) return
    await ExpenseAPI.remove(id)
    await load()
  }

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase()
    return items.filter((it) => {
      const name = (it.expense ?? '').toLowerCase()
      const type = (it.expenseType ?? '').toLowerCase()
      const passesQuery = q.length === 0 || name.includes(q) || type.includes(q)
      const passesMonth = month ? (it.date ?? '').startsWith(month) : true
      const passesMonthly = filterMode === 'all' || type.includes('monthly')
      return passesQuery && passesMonthly && passesMonth
    })
  }, [items, query, filterMode, month])

  const totalPages = useMemo(() => Math.max(1, Math.ceil(filtered.length / pageSize)), [filtered.length, pageSize])
  useEffect(() => {
    if (page > totalPages) setPage(1)
  }, [totalPages, page])

  useEffect(() => {
    setPage(1)
  }, [filterMode, month, query])

  const paginated = useMemo(() => {
    const start = (page - 1) * pageSize
    return filtered.slice(start, start + pageSize)
  }, [filtered, page, pageSize])

  const chartData = useMemo(() => {
    // Coerce nulls and unknown types; plot single amount and also split heuristic
    return filtered.map((it) => {
      const name = it.expense && it.expense.trim().length > 0 ? it.expense : 'Untitled'
      const amount = Number(it.expenseAmount ?? 0) || 0
      const type = (it.expenseType ?? '').toLowerCase()
      const isIncome = type === 'income' || type === 'salary' || type === 'credit'
      return {
        name,
        Amount: amount,
        Expense: isIncome ? 0 : amount,
        Income: isIncome ? amount : 0,
      }
    })
  }, [filtered])

  const totals = useMemo(() => {
    return filtered.reduce(
      (acc, it) => {
        const amount = Number(it.expenseAmount ?? 0) || 0
        const type = (it.expenseType ?? '').toLowerCase()
        const paymentMethod = (it.paymentMethod ?? '').toLowerCase()
        const isIncome = type === 'income' || type === 'salary' || type === 'credit'
        const isCreditCardPayment = type === 'credit card payment' || paymentMethod === 'credit card payment'
        
        acc.total += amount
        if (isIncome) {
          acc.income += amount
        } else if (isCreditCardPayment) {
          acc.creditCardPayments += amount
        } else {
          acc.expense += amount
        }
        return acc
      },
      { total: 0, income: 0, expense: 0, creditCardPayments: 0 }
    )
  }, [filtered])

  const net = totals.income - totals.expense
  const creditCardBalance = totals.expense - totals.creditCardPayments
  const bgClass = net >= 0 ? 'bg-green-50' : 'bg-red-50'

  return (
    <div className={`min-h-screen transition-colors ${bgClass}`}>
      <header className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between px-4 sm:px-6 py-4 bg-white border-b">
        <div className="flex flex-col sm:flex-row sm:items-center gap-2">
          <h1 className="text-xl sm:text-2xl font-semibold">Expense Tracker</h1>
          <div className={`px-4 py-2 rounded-full text-sm font-bold ${
            net >= 0 
              ? 'bg-green-100 text-green-800' 
              : 'bg-red-100 text-red-800'
          }`}>
            {net >= 0 ? '💰 PROFIT' : '💸 LOSS'} - ${Math.abs(net).toLocaleString()}
          </div>
        </div>
        <button onClick={logout} className="self-start sm:self-auto text-sm text-red-600 hover:underline">Logout</button>
      </header>

      <main className="max-w-7xl mx-auto p-4 sm:p-6 grid gap-4 sm:gap-6 md:grid-cols-5">
        <div className="md:col-span-5 flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="flex flex-col sm:flex-row gap-2 w-full md:w-auto">
            <input
              placeholder="Search by name or type..."
              className="w-full sm:w-72 rounded-lg border px-3 py-2"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
           
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <span className="text-sm text-gray-600">Rows</span>
            <select
              className="rounded-lg border px-2 py-1"
              value={pageSize}
              onChange={(e) => setPageSize(Number(e.target.value))}
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={20}>20</option>
            </select>
            <input
              type="month"
              className="rounded-lg border px-3 py-1"
              value={month}
              onChange={(e) => setMonth(e.target.value)}
            />
            <div className="sm:ml-2 flex items-center gap-2 w-full sm:w-auto justify-between sm:justify-start">
              <button
                className="rounded-lg border px-3 py-1 disabled:opacity-50"
                onClick={() => setPage((p) => Math.max(1, p - 1))}
                disabled={page === 1}
              >
                Prev
              </button>
              <span className="text-sm text-gray-700">Page {page} / {totalPages}</span>
              <button
                className="rounded-lg border px-3 py-1 disabled:opacity-50"
                onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                disabled={page === totalPages}
              >
                Next
              </button>
            </div>
          </div>
        </div>
        <section className="md:col-span-2 bg-white rounded-xl p-4 border">
          <h2 className="font-medium mb-3">Add Expense/Income</h2>
          <form className="space-y-3" onSubmit={onSubmit}>
            <div>
              <label className="block text-sm mb-1">Name</label>
              <input className="w-full rounded-lg border px-3 py-2" value={form.expense} onChange={(e) => setForm({ ...form, expense: e.target.value })} required />
            </div>
            <div>
              <label className="block text-sm mb-1">Type</label>
              <select className="w-full rounded-lg border px-3 py-2" value={form.expenseType} onChange={(e) => setForm({ ...form, expenseType: e.target.value })}>
                <option>Expense</option>
                <option>Income</option>
                <option>Credit Card Payment</option>
              </select>
            </div>
            <div>
              <label className="block text-sm mb-1">Payment Method</label>
              <select className="w-full rounded-lg border px-3 py-2" value={form.paymentMethod ?? 'Cash'} onChange={(e) => setForm({ ...form, paymentMethod: e.target.value })}>
                <option>Cash</option>
                <option>Credit Card</option>
                <option>Debit Card</option>
                <option>Bank Transfer</option>
                <option>Other</option>
              </select>
            </div>
            <div>
              <label className="block text-sm mb-1">Amount</label>
              <input type="number" step="0.01" className="w-full rounded-lg border px-3 py-2" value={form.expenseAmount} onChange={(e) => setForm({ ...form, expenseAmount: e.target.value })} required />
            </div>
            <div>
              <label className="block text-sm mb-1">Date</label>
              <input type="date" className="w-full rounded-lg border px-3 py-2" value={form.date ?? ''} onChange={(e) => setForm({ ...form, date: e.target.value })} />
            </div>
            <div>
              <label className="block text-sm mb-1">Quick Month (optional)</label>
              <input
                type="month"
                className="w-full rounded-lg border px-3 py-2"
                value={(form.date ?? '').slice(0, 7)}
                onChange={(e) => setForm({ ...form, date: `${e.target.value}-01` })}
              />
            </div>
            <button type="submit" className="bg-primary text-white rounded-lg px-4 py-2">{form.id ? 'Update' : 'Add'}</button>
          </form>
        </section>

        <section className="md:col-span-3 bg-white rounded-xl p-4 border">
          <h2 className="font-medium mb-3">Overview</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 mb-4 text-sm">
            <div className="rounded-lg border p-3">
              <div className="text-gray-500">Income</div>
              <div className="text-lg font-semibold text-green-600">{totals.income.toLocaleString()}</div>
            </div>
            <div className="rounded-lg border p-3">
              <div className="text-gray-500">Expenses</div>
              <div className="text-lg font-semibold text-red-600">{totals.expense.toLocaleString()}</div>
            </div>
            <div className="rounded-lg border p-3">
              <div className="text-gray-500">CC Payments</div>
              <div className="text-lg font-semibold text-blue-600">{totals.creditCardPayments.toLocaleString()}</div>
            </div>
            <div className="rounded-lg border p-3">
              <div className="text-gray-500">CC Balance</div>
              <div className={`text-lg font-semibold ${creditCardBalance > 0 ? 'text-red-600' : 'text-green-600'}`}>
                {creditCardBalance > 0 ? `+${creditCardBalance.toLocaleString()}` : creditCardBalance.toLocaleString()}
              </div>
            </div>
          </div>
          <div className="h-56 sm:h-64 md:h-72">
            {filtered.length === 0 ? (
              <div className="h-full grid place-items-center text-gray-500 text-sm">No data to display</div>
            ) : (
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData} margin={{ left: 8, right: 8, top: 8, bottom: 8 }}>
                <defs>
                  <linearGradient id="colorExpense" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#ef4444" stopOpacity={0.7}/>
                    <stop offset="95%" stopColor="#ef4444" stopOpacity={0}/>
                  </linearGradient>
                  <linearGradient id="colorIncome" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#10b981" stopOpacity={0.7}/>
                    <stop offset="95%" stopColor="#10b981" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" hide />
                <YAxis />
                <Tooltip />
                <Legend />
                <Area type="monotone" dataKey="Expense" stroke="#ef4444" fillOpacity={1} fill="url(#colorExpense)" />
                <Area type="monotone" dataKey="Income" stroke="#10b981" fillOpacity={1} fill="url(#colorIncome)" />
              </AreaChart>
            </ResponsiveContainer>
            )}
          </div>
          <div className="mt-4 overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-500 whitespace-nowrap">
                  <th className="py-2 pr-4">Name</th>
                  <th className="py-2 pr-4">Type</th>
                  <th className="py-2 pr-4">Method</th>
                  <th className="py-2 pr-4">Amount</th>
                  <th className="py-2"></th>
                </tr>
              </thead>
              <tbody>
                {loading ? (
                  <tr><td className="py-3" colSpan={5}>Loading…</td></tr>
                ) : filtered.length === 0 ? (
                  <tr><td className="py-3" colSpan={5}>No entries</td></tr>
                ) : (
                  paginated.map((it) => (
                    <tr key={it.id} className="border-t">
                      <td className="py-2 pr-4">{(it.expense && it.expense.trim().length > 0) ? it.expense : 'Untitled'}</td>
                      <td className="py-2 pr-4">{it.expenseType ?? 'Unknown'}</td>
                      <td className="py-2 pr-4">{it.paymentMethod ?? 'Cash'}</td>
                      <td className="py-2 pr-4">{Number(it.expenseAmount ?? 0).toLocaleString()}</td>
                      <td className="py-2 text-right space-x-2">
                        <button className="text-primary" onClick={() => setForm(it)}>Edit</button>
                        <button className="text-red-600" onClick={() => onDelete(it.id)}>Delete</button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  )
}


