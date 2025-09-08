import type { FormEvent } from 'react'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../store/auth'
import { ExpenseAPI } from '../lib/api'

export default function LoginPage() {
  const navigate = useNavigate()
  const login = useAuth((s) => s.login)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await login(username, password)
      // Validate credentials by hitting a protected endpoint
      await ExpenseAPI.list()
      navigate('/')
    } catch (err: any) {
      const msg = err?.response?.status === 401 ? 'Invalid username or password' : 'Login failed'
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen grid place-items-center bg-gradient-to-br from-primary-900 via-primary-800 to-primary-700 text-white p-4">
      <div className="w-full max-w-md bg-white/10 backdrop-blur rounded-xl p-8 shadow-xl">
        <h1 className="text-2xl font-semibold mb-6">Sign in</h1>
        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label className="block text-sm mb-1">Username</label>
            <input
              className="w-full rounded-lg border border-white/20 bg-white/5 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-sm mb-1">Password</label>
            <input
              type="password"
              className="w-full rounded-lg border border-white/20 bg-white/5 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {error && <p className="text-red-300 text-sm">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-accent text-black font-medium py-2 rounded-lg hover:opacity-90 transition"
          >
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
      </div>
    </div>
  )
}


